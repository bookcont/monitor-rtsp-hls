package demo.controller;

import java.io.File;

import org.apache.log4j.Logger;

import com.jfinal.core.Controller;
import com.jfinal.kit.Kv;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.template.Engine;

import demo.task.DevManager;
import demo.task.TaskManager;

public class ProcessController extends Controller{
	private Logger logger = Logger.getLogger(ProcessController.class);
	
	public void index(){
		String code = getPara(0);
		//判断设备编码不为空，且在配置文件中存在相关配置
		if(StrKit.notBlank(code)&&DevManager.isContain(code)){
			//判断是否当前对应设备的转换线程
			if(TaskManager.isContain(code)){
				//如果有，则重新激活
				TaskManager.get(code).active();
			}else{
				//如果没有，则新建
				TaskManager.create(DevManager.get(code));
			}
			//判断是否生成了转换后的m3u8文件
			String m3u8Path = Engine.use().getTemplateByString(PropKit.get("hls.m3u8.path")).renderToString(Kv.by("code", code));
			File m3u8File = new File(m3u8Path);
			//重试30次，每次间隔1秒，判断m3u8文件是否创建成功
			int retryTimes = 30;
			try {
				while(!m3u8File.exists()&&(retryTimes--)>0){
					Thread.sleep(1000);
				}
				//如果最终判断m3u8文件创建成功，则返回
				if(m3u8File.exists()){
					renderFile(m3u8File);
					return;
				}
				logger.error("请求设备[" + code + "]：m3u8文件未创建");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		//否则返回错误信息
		renderText("设备编码为空或设备未配置");
	}
}
