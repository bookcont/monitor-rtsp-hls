package com.rancedxk.monitor.task;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.rancedxk.monitor.task.DevManager.Dev;

public class TaskManager {
    private static ExecutorService taskExecutor = null;

	static Map<String,Task> tasks = new ConcurrentHashMap<String,Task>();
	
	public static void init(int maxTaskSize){
		taskExecutor = Executors.newFixedThreadPool(maxTaskSize);
	}
	
	public static Task create(Dev dev){
		if(taskExecutor!=null){
			Task task = new Task(dev);
			tasks.put(dev.getCode(), task);
			taskExecutor.execute(task);
			return task;
		}
		return null;
	}
	
	public static Map<String,Task> getAll(){
		return tasks;
	}
	
	public static Task get(String code){
		return tasks.get(code);
	}
	
	public static boolean isContain(String code){
		return tasks.containsKey(code);
	}
	
	public static Task remove(String code){
		Task task = null;
		if(tasks.containsKey(code)){
			task = tasks.remove(code);
		}
		return task;
	}
}
