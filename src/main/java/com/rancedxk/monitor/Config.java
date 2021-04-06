package com.rancedxk.monitor;

import com.rancedxk.monitor.controller.LiveController;
import com.rancedxk.monitor.controller.ProcessController;
import com.rancedxk.monitor.utils.F;
import com.rancedxk.monitor.utils.H;
import com.rancedxk.monitor.utils.N;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.template.Engine;

public class Config extends JFinalConfig{
	public static final String PROJECT_PATH = System.getProperty("user.dir").replaceAll("\\\\", "/");

	@Override
	public void configConstant(Constants me) {
		me.setDevMode(true);
	}

	@Override
	public void configRoute(Routes me) {
		me.setBaseViewPath("/page");
		me.add("/live",LiveController.class);
		me.add("/process",ProcessController.class);
	}

	@Override
	public void configEngine(Engine me) {
	}

	@Override
	public void configPlugin(Plugins me) {
	}

	@Override
	public void configInterceptor(Interceptors me) {
	}

	@Override
	public void configHandler(Handlers me) {
	}
	
	@Override
	public void afterJFinalStart() {
		//1.初始化启动Nginx
		//try {
			//关闭Nginx
			//N.stop();
			//同步配置信息
			//N.config();
			//启动Nginx
			//N.start();
		//} catch (Exception e) {
			//e.printStackTrace();
			//throw new RuntimeException("启动Nginx");
		//}

		//2.初始化创建HLS路径
		H.initHome();

		//3.关闭所有ffmpeg进程
		try {
			F.stop();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("关闭ffmpeg进程失败");
		}
	}
}
