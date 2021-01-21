package com.rancedxk.monitor.controller;

import com.jfinal.core.Controller;
import com.jfinal.kit.PropKit;

public class LiveController extends Controller{
	public void index(){
		setAttr("contextPath", PropKit.get("server.context_path"));
		setAttr("serverPort", PropKit.get("server.port"));
		render("index.html");
	}
}
