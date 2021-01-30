package com.rancedxk.monitor.task;

import java.util.HashMap;
import java.util.Map;

import com.jfinal.kit.Prop;
import com.jfinal.kit.StrKit;

public class DevManager {
	
	static Map<String,Dev> devs = null;
	
	static{
		devs = new HashMap<String, DevManager.Dev>();
		Prop prop = new Prop("monitor.properties");
		String codes = prop.get("monitor.codes");
		for(String code : codes.split(",")){
			devs.put(code, new Dev(code,prop.get(String.format("monitor.%s.title",code)),prop.get(String.format("monitor.%s.rtsp",code)),prop.get(String.format("monitor.%s.rtmp",code))));
		}
		//初始化TaskManager
		TaskManager.init(devs.size());
	}
	
	public static boolean isContain(String code){
		return devs.containsKey(code);
	}
	
	public static Dev get(String code){
		return devs.get(code);
	}
	
	public static Dev[] getAll(){
		return devs.values().toArray(new Dev[devs.size()]);
	}
	
	static class Dev{
		String code;
		String title;
		String rtsp;
		String rtmp;
		
		public Dev(String code,String title,String rtsp,String rtmp) {
			this.code = code;
			this.title = title;
			this.rtsp = rtsp;
			this.rtmp = rtmp;
		}
		
		public String getCode() {
			return code;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public String getTitle() {
			return title;
		}
		public String getStreamUrl() {
			if(StrKit.notBlank(rtsp)){
				return rtsp;
			}
			if(StrKit.notBlank(rtmp)){
				return rtmp;
			}
			return null;
		}
	}
}
