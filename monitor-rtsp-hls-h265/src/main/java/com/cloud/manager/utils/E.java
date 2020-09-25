package com.cloud.manager.utils;

import com.jfinal.kit.Kv;
import com.jfinal.kit.PropKit;
import com.jfinal.template.Engine;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * 执行系统命令
 * @author duxikun
 */
public class E {
	
	/**
	 * 执行命令行并获取进程
	 * @param rtspUrl RTSP流地址
	 * @param m3u8FileName 输出的m3u8文件名
	 * @return
	 * @throws IOException
	 */
	public synchronized static Process ffmpeg(String rtspUrl, String m3u8FileName) throws IOException {
        //从配置文件中读取命令模板，并注入相应的动态参数值
		String cmd = Engine.use()
				.getTemplateByString(PropKit.get("ffmpeg.cmd"))
				.renderToString(Kv.create()
                                    .set("ffmpeg_path", PropKit.get("ffmpeg.path"))
									.set("monitor_code_rtsp",rtspUrl)
									.set("hls_m3u8_path", m3u8FileName));
		//执行命令获取主进程
		Process process = Runtime.getRuntime().exec(cmd);
		return process;
	}
	
	/**
	 * 查询包含指定关键的进程号
	 * @param keyword 关键字
	 * @return 进程号集合
	 */
	public static List<String> q(String keyword) throws IOException {
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
	 * 杀掉包含指定关键字的进程
	 * @param keyword 关键字
	 */
	public static void k(String keyword) throws IOException, NumberFormatException, InterruptedException {
		//先查询进程号
		List<String> pids = q(keyword);
		//循环依次杀掉对应进程
		if(pids.size()!=0){
			for(String pid : pids){
				k(Integer.valueOf(pid));
			}
		}
	}
	
	/**
	 * 杀掉指定进程
	 */
	public static void k(Process process) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, IOException, InterruptedException {
		Field f = process.getClass().getDeclaredField("handle");
		f.setAccessible(true);
		long handl = f.getLong(process);
		Kernel32 kernel = Kernel32.INSTANCE;
		WinNT.HANDLE handle = new WinNT.HANDLE();
		handle.setPointer(Pointer.createConstant(handl));
		int ret = kernel.GetProcessId(handle);
		k(ret);
	}

	/**
	 * 杀掉指定进程号的进程
	 * @param pid 进程号
	 */
	public static void k(int pid) throws IOException, InterruptedException {
		String cmd = "cmd.exe /c taskkill /PID " + pid + " /F /T ";
		Runtime rt = Runtime.getRuntime();
		Process killPrcess = rt.exec(cmd);
		killPrcess.waitFor();
		killPrcess.destroy();
	}
}
