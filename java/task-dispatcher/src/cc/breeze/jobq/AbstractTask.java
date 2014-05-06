package cc.breeze.jobq;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer.Delivery;


public abstract class AbstractTask {
	private AckDelegate ackProc = null;
	private Channel channel;

	public Channel getChannel() {
		return this.channel;
	}
	
	public void setChannel(Channel channel) {
		this.channel = channel;
	}
	
	public void setAckProc(AckDelegate proc) {
		this.ackProc = proc;
	}
	
	public void ack() throws IOException {
		this.ack(false);
	}
	
	public void ack(boolean multiple) throws IOException {
		this.ackProc.ack(multiple);
	}
	
	public abstract TaskResult doTask(Delivery delivery)throws Exception;
}
