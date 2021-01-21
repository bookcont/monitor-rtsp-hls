package com.rancedxk.monitor.controller;

import java.util.Map;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.rancedxk.monitor.task.DevManager;
import com.jfinal.kit.Kv;

import cn.hutool.json.JSONUtil;

/**
 * 此处使用的websocket实现功能，没啥目的，纯粹是为了练习websocket，请无视我糟糕的代码以及日志输出
 * @author duxikun
 */
@ServerEndpoint("/api.ws")
public class LiveSocket{
	
	@OnOpen
	public void onOpen(Session session){
		System.out.println("socket open");
	}
	
	@OnMessage
	public void onMessage(String message, Session session){
		System.out.println("socket get message:"+message);
		Params params = JSONUtil.toBean(message, Params.class);
		if(params.action.equals("loadDevs")){
			sendSuccess(session, params.getAction(), DevManager.getAll());
		}
	}
    
	@OnError
	public void onError(Session session, Throwable error){
		System.out.println("err:");
		error.printStackTrace();
	}
	
	@OnClose
	public void onClose(){
		System.out.println("socket close");
	}
	
	public void sendFail(Session session,String action){
		session.getAsyncRemote().sendText(JSONUtil.toJsonStr(Kv.by("code", "fail").set("action",action)));
	}
	
	public void sendFail(Session session,String action,String message){
		session.getAsyncRemote().sendText(JSONUtil.toJsonStr(Kv.by("code", "fail").set("message",message).set("action",action)));
	}
	
	public void sendSuccess(Session session,String action, Object message){
		session.getAsyncRemote().sendText(JSONUtil.toJsonStr(Kv.by("code", "success").set("action",action).set("object",message)));
	}
	
	public static class Params{
		private String action;
		private Map<String,Object> params;

		public String getAction() {
			return action;
		}
		public void setAction(String action) {
			this.action = action;
		}
		public Map<String, Object> getParams() {
			return params;
		}
		public void setParams(Map<String, Object> params) {
			this.params = params;
		}
		public String getStr(String key){
			Object value = params.get(key);
			return value==null?null:String.valueOf(params.get(key));
		}
		public String getStr(String key,String defaultValue){
			String value = getStr(key);
			return value==null?defaultValue:value;
		}
		public int getInt(String key){
			String value = getStr(key);
			return value==null?null:Integer.valueOf(getStr(key));
		}
		public long getLong(String key){
			String value = getStr(key);
			return value==null?null:Long.valueOf(getStr(key));
		}
		public double getDouble(String key){
			String value = getStr(key);
			return value==null?null:Double.valueOf(getStr(key));
		}
	}
}
