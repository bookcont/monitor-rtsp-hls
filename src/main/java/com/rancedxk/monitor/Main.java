package com.rancedxk.monitor;

import java.io.IOException;
import java.net.DatagramSocket;

import com.rancedxk.monitor.controller.LiveSocket;
import com.jfinal.kit.PropKit;
import com.jfinal.server.undertow.UndertowConfig;
import com.jfinal.server.undertow.UndertowServer;

public class Main {
	public static void main(String[] args) throws IOException {
		//1.运行后台服务
		PropKit.use("config.properties");
		
		//2.生成随机端口
		DatagramSocket socket = null;
		int hlsServerPort = 0;
		try {
			socket = new DatagramSocket(0);
			hlsServerPort = socket.getLocalPort();
			PropKit.getProp().getProperties().setProperty("hls.server.port", String.valueOf(hlsServerPort));
		} catch (Exception e) {
			throw new RuntimeException("获取空闲端口失败");
		} finally {
			if(socket!=null&&!socket.isClosed()){
				socket.close();
			}
		}
		
		//3.运行WEB控制台
		UndertowServer
			.create(new UndertowConfig(Config.class))
			.setHost("0.0.0.0")
			.setPort(hlsServerPort)
			.setContextPath("/hls_server")
			.configWeb(builder->{
				builder.addWebSocketEndpoint(LiveSocket.class);
			})
			.start();
	}
}