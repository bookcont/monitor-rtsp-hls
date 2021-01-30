package com.rancedxk.monitor.utils;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * 浏览器相关操作
 * @author duxikun
 */
public class B {
	
	/**
	 * 打开默认浏览器访问页面
	 */
	public static void openDefault(String url){
		//启用系统默认浏览器来打开网址。
        try {
            URI uri = new URI(url);
            Desktop.getDesktop().browse(uri);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

	/**
	 * 打开IE浏览器访问页面
	 */
	public static void openIE(String url) {
		//启用cmd运行IE的方式来打开网址。
		String str = "cmd /c start iexplore " + url;
		try {
			Runtime.getRuntime().exec(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
