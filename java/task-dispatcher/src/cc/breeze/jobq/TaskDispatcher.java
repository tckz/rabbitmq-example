package cc.breeze.jobq;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cc.breeze.NotImplementedException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;


public class TaskDispatcher {
	
	public static void run(String uriString, Collection<TaskInfo>taskInfos) throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
	    factory.setUri(uriString);
	    Connection connection = factory.newConnection();
	    
	    try {
	    	new TaskDispatcher().dispatcherMain(connection, taskInfos);
	    } finally {
	    	connection.close();
	    }
	}
	
	public void dispatcherMain(Connection connection, Collection<TaskInfo>taskInfos) throws Exception {
		
		final ExecutorService executor = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
            60L, TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>(),
            new ThreadFactory() {
				private int count = 0;
				@Override
				public Thread newThread(Runnable r) {
					Thread t = new Thread(r);
					count++;
					t.setName("worker" + count);
					return t;
				}
			}
		);

		try {
			final CompletionService<TaskResult> svc = new ExecutorCompletionService<TaskResult>(executor);
			
			Future<Integer>waitTask = executor.submit(new Callable<Integer>(){
				@Override
				public Integer call() throws Exception {
					Thread.currentThread().setName("taskWait");
					for (;;) {
						Future<TaskResult>task = svc.take();
						task.get();
					}
				}
			});
			
	    	this.prepareTasks(svc, connection, taskInfos);
		    waitTask.get();
		
		} finally {
			executor.shutdownNow();
		}
	}
	
	private void prepareTasks(CompletionService<TaskResult>svc, Connection connection, Collection<TaskInfo>taskInfos) throws Exception {
	    for (final TaskInfo info : taskInfos) {
	    	// インスタンス化可能か確認しておく
	    	this.instantiateTask(info.getImplClass());
	    	
		    final Channel channel = connection.createChannel();
		    final QueueingConsumer consumer = this.prepareChannelAndConsumer(info, channel);
		    
		    svc.submit(new Callable<TaskResult>(){
				@Override
				public TaskResult call() throws Exception {
					Thread currentThread = Thread.currentThread();
					String threadName = String.format("%s[%s]", info, currentThread.getId());
					currentThread.setName(threadName);
					
				    for (;;) {
				    	AbstractTask taskImpl = instantiateTask(info.getImplClass());
						final QueueingConsumer.Delivery delivery = consumer.nextDelivery();
				    	taskImpl.setChannel(channel);
			    		taskImpl.setAckProc(new AckDelegate() {
							@Override
							public void ack(boolean multiple) throws IOException {
						    	if (info.isAutoAck()) {
						    		throw new RuntimeException("Configured autoAck. " + info.toString());
						    	} else {
								    long deliveryTag = delivery.getEnvelope().getDeliveryTag();
									channel.basicAck(deliveryTag, multiple);
						    	}
							}
						});
						
						taskImpl.doTask(delivery);
				    }
				}
			});
	    }
		
	}
	
	private QueueingConsumer prepareChannelAndConsumer(TaskInfo info, Channel channel) throws Exception {
		
		String queueName;
		if (info instanceof TaskQueueInfo) {
			TaskQueueInfo qi = (TaskQueueInfo)info;
			queueName = qi.getQueueName();
		    channel.queueDeclare(queueName, info.isDurable(), info.isExclusive(), false, null);
		} else if (info instanceof TaskTopicInfo){
			TaskTopicInfo ti = (TaskTopicInfo)info;
			String exchange = ti.getExchange();
			channel.exchangeDeclare(exchange, "topic", info.isDurable(), false, null);
			queueName = channel.queueDeclare().getQueue();
			for (String routingKey : ti.getBindings()) {
				channel.queueBind(queueName, exchange, routingKey);
			}
		} else {
			throw new NotImplementedException("Unknown: " + info.getClass().getName());
		}

		channel.basicQos(1, false);
		QueueingConsumer consumer = new QueueingConsumer(channel);
	    channel.basicConsume(queueName, info.isAutoAck(), consumer);
		
		return consumer;

	}
	
	private AbstractTask instantiateTask(Class<? extends AbstractTask>cls) throws Exception {
		Object o = cls.newInstance();
		return (AbstractTask)o;
	}
}

