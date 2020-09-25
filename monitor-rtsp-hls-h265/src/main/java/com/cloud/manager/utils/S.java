package com.cloud.manager.utils;

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
}
