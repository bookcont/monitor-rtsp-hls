package com.rancedxk.monitor.utils;

import java.io.File;
import java.io.IOException;

import com.rancedxk.monitor.Config;
import com.jfinal.core.JFinal;
import com.jfinal.kit.Kv;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;

/**
 * Nginx操作类
 * @author duxikun
 */
public class N {
	
	private static String basePath = Config.PROJECT_PATH + "/third/nginx";
	private static String hlsPath = Config.PROJECT_PATH + "/third/hls";
	
	public static void config() {
		File configFile = new File(basePath + "/conf/nginx.conf");
		if(configFile.exists()){
			configFile.delete();
		}
		String nginxTemplateStr = ResourceUtil.readUtf8Str("nginx.template");
		String nginxConfigStr = S.t(nginxTemplateStr, Kv.create()
														.set("context_path",S.tc("server.context_path"))
														.set("nginx_port",S.tc("server.port"))
														.set("nginx_domain",S.tc("server.domain"))
														.set("hls_server_port",S.tc("hls.server.port"))
														.set("hls_path",hlsPath));
		FileUtil.writeString(nginxConfigStr, configFile, JFinal.me().getConstants().getEncoding());
	}
	public static void start() throws IOException, InterruptedException {
		//执行命令获取主进程
		Runtime.getRuntime().exec("cmd.exe /c nginx.exe",new String[]{},new File(basePath));
	}
	public static void stop() throws IOException, InterruptedException {
		//执行命令获取主进程
		Runtime.getRuntime().exec("cmd.exe /c nginx.exe -s stop",new String[]{},new File(basePath)).waitFor();
	}
}
