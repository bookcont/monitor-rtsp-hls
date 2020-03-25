package com.rancedxk.monitor.task;

import java.util.HashMap;
import java.util.Map;

import com.jfinal.kit.Prop;

public class DevManager {
	
	static Map<String,Dev> devs = null;
	
	static{
		devs = new HashMap<String, DevManager.Dev>();
		Prop prop = new Prop("monitor.properties");
		String codes = prop.get("monitor.codes");
		for(String code : codes.split(",")){
			devs.put(code, new Dev(code,prop.get(String.format("monitor.%s.rtsp",code))));
		}
	}
	
	public static boolean isContain(String code){
		return devs.containsKey(code);
	}
	
	public static Dev get(String code){
		return devs.get(code);
	}
	
	static class Dev{
		String code;
		String rtsp;
		
		public Dev(String code,String rtsp) {
			this.code = code;
			this.rtsp = rtsp;
		}
		
		public String getCode() {
			return code;
		}
		public String getRtsp() {
			return rtsp;
		}
	}
}
