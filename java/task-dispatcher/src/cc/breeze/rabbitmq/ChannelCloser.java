package cc.breeze.rabbitmq;

import com.rabbitmq.client.Channel;

public class ChannelCloser implements AutoCloseable {

	private final Channel channel;
	public ChannelCloser(Channel channel) {
		this.channel = channel;
	}
	
	public Channel getChannel() {
		return this.channel;
	}
	
	@Override
	public void close() throws Exception {
		this.channel.close();
	}

}
