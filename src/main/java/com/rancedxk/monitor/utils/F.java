package com.rancedxk.monitor.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.rancedxk.monitor.Config;
import com.jfinal.kit.Kv;

/**
 * 执行系统命令
 * @author duxikun
 */
public class F {
	
	private static String basePath = Config.PROJECT_PATH + "/third/ffmpeg";
	
	/**
	 * 执行命令行并获取进程
	 * @param rtspUrl RTSP流地址
	 * @param m3u8FileName 输出的m3u8文件名
	 * @return
	 * @throws IOException
	 */
	public synchronized static Process ffmpeg(String rtspUrl, String m3u8FileName) throws IOException {
        //从配置文件中读取命令模板，并注入相应的动态参数值
		String cmd = S.tc("ffmpeg.cmd",Kv.create()
                                    .set("ffmpeg_path", basePath)
									.set("monitor_code_rtsp",rtspUrl)
									.set("hls_m3u8_path", m3u8FileName));
		//执行命令获取主进程
		Process process = Runtime.getRuntime().exec(cmd);
		return process;
	}
	
	/**
	 * 杀掉所有ffmpeg进程
	 */
	public static void stop() throws IOException, NumberFormatException, InterruptedException {
		//先查询进程号
		List<String> pids = q("ffmpeg.exe");
		//循环依次杀掉对应进程
		if(pids.size()!=0){
			for(String pid : pids){
				k(Integer.valueOf(pid));
			}
		}
	}
	
	/**
	 * 查询包含指定关键的进程号
	 * @param keyword 关键字
	 * @return 进程号集合
	 */
	private static List<String> q(String keyword) throws IOException {
		Process process = Runtime.getRuntime().exec("tasklist");
		Scanner in = new Scanner(process.getInputStream());
		List<String> pids = new ArrayList<String>();
		while(in.hasNextLine()){
			String p = in.nextLine();
			if(p.contains(keyword)){
				pids.add(p.replaceAll("\\s{1,}",",").split(",")[1]);
			}
		}
		in.close();
		return pids;
	}
	
	/**
	 * 杀掉指定进程号的进程
	 * @param pid 进程号
	 */
	private static void k(int pid) throws IOException, InterruptedException {
		String cmd = "cmd.exe /c taskkill /PID " + pid + " /F /T ";
		Runtime rt = Runtime.getRuntime();
		Process killPrcess = rt.exec(cmd);
		killPrcess.waitFor();
		killPrcess.destroy();
	}
}
