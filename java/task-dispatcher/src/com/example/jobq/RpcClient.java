package com.example.jobq;

import java.util.Properties;
import java.util.UUID;

import cc.breeze.AutoCloser;
import cc.breeze.rabbitmq.ChannelCloser;
import cc.breeze.rabbitmq.ConnectionCloser;

import com.google.common.base.Charsets;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.QueueingConsumer.Delivery;


public class RpcClient {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		Properties prop = Dispatcher.loadProperties();

		String uriString = prop.getProperty("mq.url");
		String queueName = "rpcq";
		 
	    ConnectionFactory factory = new ConnectionFactory();
	    factory.setUri(uriString);
	    try (AutoCloser closer = new AutoCloser()) {
		    Connection connection = factory.newConnection();
		    closer.add(new ConnectionCloser(connection));
			Channel channel = connection.createChannel();
			closer.add(new ChannelCloser(channel));
		    
		    channel.queueDeclare(queueName, true, false, false, null);
	
		    String callbackQueueName = channel.queueDeclare().getQueue();
		    
		    UUID uuid = UUID.randomUUID();
		    
		    BasicProperties props = new BasicProperties
	                .Builder()
	                .replyTo(callbackQueueName)
	                .messageId(uuid.toString())
	                .build();
		    
		    String message = "Hello " + uuid.toString();
		    channel.basicPublish("", queueName, props, message.getBytes());
		    System.out.println("Sent: " + message);
		    
		    // waiting return
		    QueueingConsumer consumer = new QueueingConsumer(channel);
		    channel.basicConsume(callbackQueueName, true, consumer);
		    Delivery delivery = consumer.nextDelivery();
		    
			String body = new String(delivery.getBody(), Charsets.UTF_8);
			System.out.printf("Return: %s\n", body);

	    }
	}

}
