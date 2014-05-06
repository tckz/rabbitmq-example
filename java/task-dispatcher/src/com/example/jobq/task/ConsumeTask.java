package com.example.jobq.task;

import cc.breeze.jobq.AbstractTask;
import cc.breeze.jobq.TaskResult;

import com.google.common.base.Charsets;
import com.rabbitmq.client.QueueingConsumer.Delivery;


public class ConsumeTask extends AbstractTask {

	@Override
	public TaskResult doTask(Delivery delivery) throws Exception {
		
		String threadName = Thread.currentThread().getName();
		
		String body = new String(delivery.getBody(), Charsets.UTF_8);
		System.out.printf("%s: %s\n", threadName, body);
		
		this.ack();
		
		return null;
	}

}
