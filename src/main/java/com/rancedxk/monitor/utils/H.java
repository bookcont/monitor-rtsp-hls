package com.rancedxk.monitor.utils;

import java.io.File;

import com.rancedxk.monitor.Config;

import cn.hutool.core.io.FileUtil;

/**
 * hls相关操作
 * @author duxikun
 */
public class H {
	
	private static String basePath = Config.PROJECT_PATH + "/third/hls";
	
	/**
	 * 初始化HLS所有路径
	 */
	public static void initHome(){
		if(!FileUtil.exist(basePath)){
			FileUtil.mkdir(basePath);
		}
	}
	
	/**
	 * 获取基于HLS目录的相对路径
	 * @param dirs 目录路径
	 */
	public static String getPath(String... dirs){
		StringBuffer sBuffer = new StringBuffer(basePath);
		for(String dir : dirs){
			sBuffer.append(File.separator).append(dir);
		}
		return sBuffer.toString();
	}
}
