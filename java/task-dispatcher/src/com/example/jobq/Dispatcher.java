package com.example.jobq;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import com.example.jobq.task.ConsumeTask;
import com.example.jobq.task.RpcTask;
import com.example.jobq.task.SubscribeTask;
import com.google.common.base.Charsets;

import cc.breeze.jobq.TaskDispatcher;
import cc.breeze.jobq.TaskInfo;


public class Dispatcher {

	public static void main(String[] args) throws Exception {

		List<TaskInfo> taskInfos = Arrays.asList(new TaskInfo[] {
			TaskInfo.queue("helloq", ConsumeTask.class, false, false),
			TaskInfo.queue("helloq", ConsumeTask.class, false, false),
			TaskInfo.queue("rpcq", RpcTask.class, false, false),
			TaskInfo.topic("mytopic", new String[] {"com.example.*", "com.example"} , SubscribeTask.class, false),
			TaskInfo.topic("mytopic", new String[] {"#"} , SubscribeTask.class, false),
		});
			
		Properties prop = loadProperties();
		TaskDispatcher.run(prop.getProperty("mq.url"), taskInfos);
	}

	
	public static Properties loadProperties() throws IOException {
		Properties prop = new Properties();
		Path path = Paths.get("config", "jobq.properties");
		prop.load(Files.newBufferedReader(path, Charsets.UTF_8));
		
		return prop;
	}
}
