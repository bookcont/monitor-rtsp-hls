package com.rancedxk.monitor.task;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.rancedxk.monitor.task.DevManager.Dev;
import com.rancedxk.monitor.utils.F;
import com.rancedxk.monitor.utils.H;
import com.rancedxk.monitor.utils.S;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;

import cn.hutool.core.util.ReUtil;

public class Task implements Runnable{
    private Logger logger = Logger.getLogger(getClass());

    private boolean isRunning = false;
    //记录线程开始时间
    private long startTime;
    //转换可持续最大时间，达到时间则自动结束
    private static final long duration = PropKit.getLong("hls.duration");
    //设备信息
    private Dev dev;
    //HLS目录
    private String m3u8Path;

    public Task(Dev dev) {
        this.dev = dev;
        this.m3u8Path = H.getPath(dev.getCode(),"index.m3u8");
    }

    public Dev getDev() {
        return dev;
    }

    public boolean isRunning() {
        return isRunning;
    }

    /**
     * 设置开始时间为当前时间，以此来延长转换持续时间
     */
    public void active() {
        if(!this.isRunning){
            //如果当前已经停止，则重新运行
            this.run();
        }else{
            //如果正在运行，则重新设置startTime
            this.startTime = System.currentTimeMillis();
        }
    }

    @Override
    public void run() {
        //开始
        isRunning = true;
        String dir = H.getPath(dev.getCode());
        //创建文件夹
        createDir(dir);
        //运行，设置视频源和推流地址和HLS目录
        convert(dev.getRtsp(),dev.getCode());
        //运行结束，从池中移除
        TaskManager.remove(dev.getCode());
        //删除文件夹
        deleteDir(dir);
        //结束
        isRunning = false;
    }

    private void convert(String rtsp, String code) {
        //检测RTSP流服务是否可访问
        if(!isConnectable(rtsp)){
            return;
        }
        try {
            logger.info(S.f("设备[%s]：%s", code, "开始转码"));
            Process process = F.ffmpeg(rtsp, this.m3u8Path);
            InputStream is = process.getErrorStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            this.startTime = System.currentTimeMillis();
            while(System.currentTimeMillis()-startTime < duration && (line = br.readLine()) != null) {
            	if(PropKit.getBoolean("ffmpeg.log")){
            		logger.error(S.f("设备[%s]： %s", code, line));
            	}
            }
            //关闭流，释放资源
            if(process != null){
                process.destroy();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            logger.info(S.f("设备[%s]：%s", code, "结束转码"));
		}
    }

    /**
     * 检测指定IP+端口是否可连接
     * @param rtsp
     * @return
     */
    private boolean isConnectable(String rtsp) {
        if(StrKit.isBlank(rtsp)){
            return false;
        }
        String ip = ReUtil.get(Pattern.compile("(\\d+\\.\\d+\\.\\d+\\.\\d+):(\\d+)"), rtsp, 1);
        String port = ReUtil.get(Pattern.compile("(\\d+\\.\\d+\\.\\d+\\.\\d+):(\\d+)"), rtsp, 2);
        if(StrKit.isBlank(ip)||StrKit.isBlank(port)){
            return true;
        }
        Socket socket = null;
        try {
            if(InetAddress.getByName(ip).isReachable(3000)){
                socket = new Socket();
                socket.connect(new InetSocketAddress(ip, Integer.valueOf(port)));
                return true;
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(socket!=null){
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
    /**
     * 创建文件夹
     * @param dir
     * @return true:正确 false:错误
     */
    private boolean createDir(String dir){
        File file = new File(dir);
        return file.mkdir();
    }
    /**
     * 删除文件夹
     * @param dir
     */
    private void deleteDir(String dir){
        File file = new File(dir);
        //删除文件夹
        if(file.isFile()){
            file.delete();
        }else{
            File[] files = file.listFiles();
            if(files == null){
                file.delete();
            }else{
                for (int i = 0; i < files.length; i++) {
                    deleteDir(files[i].getAbsolutePath());
                }
                file.delete();
            }
        }
    }
}
