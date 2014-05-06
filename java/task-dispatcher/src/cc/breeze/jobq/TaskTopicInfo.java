package cc.breeze.jobq;

import java.util.Collection;

import com.google.common.collect.Lists;


public class TaskTopicInfo extends TaskInfo {
	private final Collection<String>bindings = Lists.newArrayList();
	private String exchange;

	public Collection<String> getBindings() {
		return bindings;
	}

	public String getExchange() {
		return exchange;
	}

	public void setExchange(String exchange) {
		this.exchange = exchange;
	}


	@Override
	public String toString() {
		return this.getExchange() + bindings;
	}

}
