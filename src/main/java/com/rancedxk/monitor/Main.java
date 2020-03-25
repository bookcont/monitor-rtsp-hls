package com.rancedxk.monitor;

import com.jfinal.kit.PropKit;
import com.jfinal.server.undertow.UndertowConfig;
import com.jfinal.server.undertow.UndertowServer;

public class Main {
	public static void main(String[] args) {
		//1.运行后台服务
		PropKit.use("config.properties");
		
		//2.运行WEB控制台
		UndertowServer
		.create(new UndertowConfig(Config.class))
		.setHost("0.0.0.0")
		.setPort(PropKit.getInt("server.port"))
		.start();
	}
}
