package com.rancedxk.monitor.utils;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 配置文件读取工具类
 */
public class C {
	private static Logger logger = Logger.getLogger(C.class);
	//配置文件路径
	private static String filePath = null;
	//配置项
	private static ConfigBuffer params = null;

	public static void u(String filePath){
		C.filePath = filePath;
		try {
			params = ConfigBuffer.load(filePath);
		} catch (Exception e) {
			logger.error("读取配置文件失败：",e);
		}
	}
	
	private static ConfigBuffer getProp() {
		if (params == null) {
			throw new IllegalStateException("未加载配置文件");
		}
		return params;
	}
	
	/**
	 * 重新设置配置信息<br/>
	 * 注：此方法不向配置文件中物理输出，如果需要物理保存，请继续调用saveConfig方法
	 * @param key 键
	 * @param value 值
	 */
	public static void s(String key, String value){
		getProp().set(key, value).store();;
	}
	
	/**
	 * 从配置文件中获取key所对应的值
	 * @param key 键
	 * @return value 值
	 */
	public static String g(String key){
		return getProp().get(key);
	}
	
	/**
	 * 从配置文件中获取key所对应的值
	 * @param key 键
	 * @param defaultValue 键不存在时，返回该默认值
	 * @return value 值
	 */
	public static String g(String key, String defaultValue){
		String value = g(key);
		return value == null ? defaultValue : value;
	}

	/**
	 * 获取指定key对应的值，转换为int型
	 */
	public static Integer gi(String key) {
		return gi(key, null);
	}

	/**
	 * 获取指定key对应的值，转换为int型
	 */
	public static Integer gi(String key, Integer defaultValue) {
		String value = g(key);
		return (value != null) ? Integer.parseInt(value) : defaultValue;
	}

	/**
	 * 获取指定key对应的值，转换为long型
	 */
	public static Long gl(String key) {
		return gl(key, null);
	}

	/**
	 * 获取指定key对应的值，转换为long型
	 */
	public static Long gl(String key, Long defaultValue) {
		String value = g(key);
		return (value != null) ? Long.parseLong(value) : defaultValue;
	}
	
	/**
	 * 获取指定key对应的值，转换为boolean型
	 */
	public static Boolean gb(String key) {
		return gb(key, null);
	}

	/**
	 * 获取指定key对应的值，转换为boolean型
	 */
	public static Boolean gb(String key, Boolean defaultValue) {
		String value = g(key);
		return (value != null) ? Boolean.parseBoolean(value) : defaultValue;
	}
	
	public static boolean c(String key) {
		return params.containsKey(key);
	}
	
	public static interface ConfigLine{
	}
	
	public static class ConfigBlank implements ConfigLine{
	}
	
	public static class ConfigComment implements ConfigLine{
		private String comment;
		public ConfigComment(String comment) {
			this.comment = comment;
		}
		public String get(){
			return comment;
		}
	}
	
	public static class ConfigParam implements ConfigLine{
		private String key;
		private String value;
		
		public ConfigParam(String key, String value) {
			this.key = key;
			this.value = value;
		}
		
		public String getKey(){
			return this.key;
		}
		
		public ConfigLine set(String value){
			this.value = value;
			return this;
		}
		
		public String get(){
			return this.value;
		}
		
		public int getInt(){
			return Integer.valueOf(this.value);
		}
		
		public long getLong(){
			return Long.valueOf(this.value);
		}
		
		public boolean isTrue(){
			return Boolean.valueOf(this.value);
		}
		
		public boolean isFalse(){
			return !isTrue();
		}
	}
	
	public static class ConfigBuffer{
		private String fileName = null;
		private Map<String,ConfigLine> lines = null;
		
		public ConfigBuffer set(String key, String value){
			if(lines.containsKey(key)){
				((ConfigParam)lines.get(key)).set(value);
			}
			return this;
		}
		
		public String get(String key){
			if(containsKey(key)){
				return ((ConfigParam)lines.get(key)).get();
			}
			return null;
		}
		
		public boolean containsKey(String key){
			return lines.containsKey(key);
		}
		
		public static ConfigBuffer load(String fileName) throws Exception {
			InputStream inputStream = C.class.getClassLoader().getResourceAsStream(fileName);
			if(inputStream!=null){
				ConfigBuffer buffer = new ConfigBuffer();
				buffer.fileName = fileName;
				buffer.lines = new LinkedHashMap<String,ConfigLine>();
				BufferedReader reader = null;
				try {
					reader = new BufferedReader(new InputStreamReader(inputStream));
					int lineNum = 0;
					while (true) {
						String lineStr = reader.readLine();
						if(lineStr==null)break;
						if(lineStr.trim().equals("")){
							buffer.lines.put(S.f("%02d", ++lineNum), new ConfigBlank());
						}else if(lineStr.startsWith("#")){
							buffer.lines.put(S.f("%02d", ++lineNum), new ConfigComment(lineStr.substring(1)));
						}else{
							String key = lineStr.split("=")[0].trim();
							String value = lineStr.split("=")[1].trim();
							buffer.lines.put(key, new ConfigParam(key,value));
						}
					}
					return buffer;
				}catch (Exception e){
					throw new RuntimeException("加载配置文件失败", e);
				}finally{
					if(reader!=null){
						try {
							reader.close();
						} catch (IOException e) {
							logger.error("",e);
						}
					}
				}
			}else{
				throw new IllegalArgumentException(S.f("配置文件[%s]不存在",filePath));
			}
		}
		
		public void store(){
			String filePath = C.class.getClassLoader().getResource(fileName).getFile();
			File file = new File(filePath);
			if(file.exists()){
				file.delete();
			}
			FileWriter writer = null;
			try {
				StringBuilder fileContent = new StringBuilder();
				for(ConfigLine line : lines.values()){
					if(line instanceof ConfigBlank){
						fileContent.append("\n");
					}else if(line instanceof ConfigComment){
						fileContent.append("#");
						fileContent.append(((ConfigComment)line).get());
						fileContent.append("\n");
					}else if(line instanceof ConfigParam){
						fileContent.append(((ConfigParam)line).getKey());
						fileContent.append("=");
						fileContent.append(((ConfigParam)line).get());
						fileContent.append("\n");
					}
				}
				writer = new FileWriter(file);
				writer.write(fileContent.toString());
				writer.flush();
			}catch (Exception e){
				throw new RuntimeException("保存配置文件失败", e);
			}finally{
				if(writer!=null){
					try {
						writer.close();
					} catch (IOException e) {
						logger.error("",e);
					}
				}
			}
		}
	}
}
