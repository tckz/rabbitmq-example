package com.example.jobq.task;

import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import cc.breeze.jobq.AbstractTask;
import cc.breeze.jobq.TaskResult;

import com.google.common.base.Charsets;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.QueueingConsumer.Delivery;


public class RpcTask extends AbstractTask {

	@Override
	public TaskResult doTask(Delivery delivery) throws Exception {
		
		String body = new String(delivery.getBody(), Charsets.UTF_8);
		DateTime now = new DateTime(DateTimeZone.forTimeZone(TimeZone.getTimeZone("JST")));

		String response = now.toString() + ":" + body;
		
		BasicProperties props = delivery.getProperties();
		this.getChannel().basicPublish("", props.getReplyTo(), null, response.getBytes());
		
		this.ack();
		
		return null;
	}

}
