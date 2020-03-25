package com.rancedxk.monitor.task;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.rancedxk.monitor.task.DevManager.Dev;

public class TaskManager {
	private static Logger logger = Logger.getLogger(TaskManager.class);

	/**线程数*/
    private static int threadNum = Runtime.getRuntime().availableProcessors() < 4 ? 3 : Runtime.getRuntime().availableProcessors();
    /**共享给Worker的线程数*/
    private static int shareToWorkerThreadNum = threadNum > 4 ? threadNum >> 2 : threadNum - 2;
    /**Worker线程数*/
    private static int workerThreadNum = threadNum - shareToWorkerThreadNum;
	
	private static ExecutorService taskExecutor = Executors.newFixedThreadPool(workerThreadNum);

	static Map<String,Task> tasks = new ConcurrentHashMap<String,Task>();
	
	public static Task create(Dev dev){
		logger.error(String.format("创建任务：%s",dev.getCode()));
		Task task = new Task(dev);
		tasks.put(dev.getCode(), task);
		taskExecutor.execute(task);
		return task;
	}
	
	public static Task get(String code){
		return tasks.get(code);
	}
	
	public static boolean isContain(String code){
		return tasks.containsKey(code);
	}
	
	public static Task remove(String code){
		logger.error(String.format("移除任务：%s",code));
		Task task = null;
		if(tasks.containsKey(code)){
			task = tasks.remove(code);
		}
		return task;
	}
}
