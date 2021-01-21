package com.rancedxk.monitor.controller;

import org.apache.log4j.Logger;

import com.rancedxk.monitor.task.DevManager;
import com.rancedxk.monitor.task.TaskManager;
import com.rancedxk.monitor.utils.H;
import com.jfinal.core.Controller;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;

import cn.hutool.core.io.FileUtil;

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
            String m3u8Path = H.getPath(code,"index.m3u8");
            //每次间隔1秒，循环判断m3u8文件是否创建成功
            long retryTimes = PropKit.getLong("hls.duration")/1000;
            try {
                while(!FileUtil.exist(m3u8Path)&&(retryTimes--)>0){
                    Thread.sleep(1000);
                }
                //如果最终判断m3u8文件创建成功，则返回
                if(FileUtil.exist(m3u8Path)){
                    renderFile(FileUtil.file(m3u8Path));
                    return;
                }
                logger.error("请求设备[" + code + "]：m3u8文件未创建");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //否则返回404
        renderError(404);
    }
}
