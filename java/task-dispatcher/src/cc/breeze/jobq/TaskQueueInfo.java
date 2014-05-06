package cc.breeze.jobq;



public class TaskQueueInfo extends TaskInfo {
	private String queueName;

	public String getQueueName() {
		return queueName;
	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	@Override
	public String toString() {
		return this.getQueueName();
	}
}