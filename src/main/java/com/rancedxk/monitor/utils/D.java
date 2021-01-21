package com.rancedxk.monitor.utils;

import cn.hutool.core.util.StrUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期时间工具类
 */
public class D {
	
	/**
	 * 默认格式：YYYY-MM-dd HH:mm:ss
	 */
	public final static String FORMAT_DEF = "YYYY-MM-dd HH:mm:ss";
	/**
	 * 格式1：yyyyMMdd
	 */
	public final static String FORMAT1 = "yyyyMMdd";
	/**
	 * 格式2：yyyy-MM-dd
	 */
	public final static String FORMAT2 = "yyyy-MM-dd";
	/**
	 * 格式3：yyyyMMddHHmmss
	 */
	public final static String FORMAT3 = "yyyyMMddHHmmss";
	/**
	 * 格式4：yyMMddHHmmss
	 */
	public final static String FORMAT4 = "yyMMddHHmmss";
	/**
	 * 格式5：HH:mm:ss
	 */
	public final static String FORMAT5 = "HH:mm:ss";
	
	/**
	 * 以默认格式获取当前日期
	 */
	public static String g(){
		return g(FORMAT_DEF);
	}
	
	/**
	 * 以指定格式获取当前日期，格式为null时使用默认格式
	 * @param format 日期格式
	 */
	public static String g(String format){
		if(StrUtil.isBlank(format)){
			format = FORMAT_DEF;
		}
		return new SimpleDateFormat(format).format(new Date());
	}
	
	/**
	 * 以默认格式来格式化指定日期对象
	 * @param date 日期对象
	 */
	public static String f(Date date){
		if(date==null){
			return g();
		}
		return new SimpleDateFormat(FORMAT_DEF).format(date);
	}
	
	/**
	 * 使用指定格式来格式化指定日期对象
	 * @param date 日期对象
	 * @param format 日期格式
	 */
	public static String f(Date date, String format){
		if(date==null){
			return g(format);
		}else if(StrUtil.isBlank(format)){
			format = FORMAT_DEF;
		}
		return new SimpleDateFormat(format).format(date);
	}
}
