package cc.breeze.jobq;

import java.util.Arrays;
import java.util.Collection;



public class TaskInfo {
	private boolean autoAck = true;
	private boolean exclusive = false;
	private boolean durable = true;
	private Class<? extends AbstractTask>implClass;

	protected TaskInfo() {
		
	}

	public boolean isAutoAck() {
		return autoAck;
	}

	public void setAutoAck(boolean autoAck) {
		this.autoAck = autoAck;
	}

	public boolean isExclusive() {
		return exclusive;
	}

	public void setExclusive(boolean exclusive) {
		this.exclusive = exclusive;
	}

	public boolean isDurable() {
		return durable;
	}

	public void setDurable(boolean durable) {
		this.durable = durable;
	}

	public Class<? extends AbstractTask> getImplClass() {
		return implClass;
	}

	public void setImplClass(Class<? extends AbstractTask> implClass) {
		this.implClass = implClass;
	}

	public static TaskInfo topic(String exchangeName, String[] bindings, Class<? extends AbstractTask>implClass) {
		return topic(exchangeName, Arrays.asList(bindings), implClass, true, true);
	}
	
	public static TaskInfo topic(String exchangeName, String[] bindings, Class<? extends AbstractTask>implClass, boolean autoAck) {
		return topic(exchangeName, Arrays.asList(bindings), implClass, autoAck, true);
	}
	
	public static TaskInfo topic(String exchangeName, Collection<String>bindings, Class<? extends AbstractTask>implClass, boolean autoAck, boolean durable) {
		TaskTopicInfo ret = new TaskTopicInfo();

		ret.setDurable(durable);
		ret.setExchange(exchangeName);
		ret.getBindings().addAll(bindings);
		ret.setImplClass(implClass);
		ret.setAutoAck(autoAck);
		
		return ret;
	}
	
	
	public static TaskInfo queue(String queueName, Class<? extends AbstractTask>implClass, boolean autoAck, boolean exclusive) {
		TaskQueueInfo ret = new TaskQueueInfo();
		ret.setQueueName(queueName);
		ret.setExclusive(exclusive);
		ret.setImplClass(implClass);
		ret.setAutoAck(autoAck);
		
		return ret;
	}
}
