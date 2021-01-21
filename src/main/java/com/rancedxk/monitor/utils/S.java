package com.rancedxk.monitor.utils;

import com.jfinal.kit.Kv;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.template.Engine;

/**
 * 字符串模板格式化输出
 * @author duxikun
 */
public class S {
	
	/**
	 * 按模板格式化字符串
	 * @param str 模板串，如："%s"
	 * @param args 参数集
	 */
	public static String f(String str, Object... args) {
		return String.format(str, args);
	}
	
	/**
	 * 拼接字符串参数
	 * @param strs 字符串参数集
	 */
	public static String j(String... strs){
		return j("",strs);
	}
	
	/**
	 * 拼接字符串参数，以分隔符分隔
	 * @param divide 分隔符
	 * @param strs 字符串参数集
	 */
	public static String j(String divide, String... strs){
		if(strs==null||strs.length==0){
			return "";
		}
		StringBuilder sBuilder = new StringBuilder();
		for(int i=0;i<strs.length;i++){
			sBuilder.append(strs[i]);
			if(i<strs.length-1){
				sBuilder.append(divide);
			}
		}
		return sBuilder.toString();
	}
	
	/**
	 * 解析模板字符串
	 * @param templateStr 模板字符串，详见jfinal enjoy模板文档
	 * @param params 占位参数集
	 * @return 解析结果
	 */
	public static String t(String templateStr, Kv params){
		if(StrKit.isBlank(templateStr)){
			return null;
		}
		if(params==null){
			params = Kv.create();
		}
		params.set("project_path", System.getProperty("user.dir"));
		return Engine.use()
			.getTemplateByString(templateStr)
			.renderToString(params);
	}
	
	/**
	 * 解析配置文件中的配置项模板字符串
	 * @param configKey 配置文件中的配置项KEY
	 * @param params 占位参数集
	 * @return 解析结果
	 */
	public static String tc(String configKey, Kv params){
		return t(PropKit.get(configKey),params);
	}
	
	/**
	 * 解析配置文件中的配置项模板字符串
	 * @param configKey 配置文件中的配置项KEY
	 * @param params 占位参数集
	 * @return 解析结果
	 */
	public static String tc(String configKey){
		return t(PropKit.get(configKey),null);
	}
}
