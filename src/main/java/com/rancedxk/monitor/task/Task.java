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

public class Task implements Runnable {
    private Logger logger = Logger.getLogger(getClass());

    private boolean isRunning = false;
    //记录线程开始时间
    private long startTime;
    //转换可持续最大时间，达到时间则自动结束
    private static final long duration = PropKit.getLong("hls.duration");
    //设备信息
    private Dev dev;
    //设备HLS目录路径
    private String dirPath;
    //M3U8文件路径
    private String m3u8Path;

    public Task(Dev dev) {
        this.dev = dev;
        this.dirPath = H.getPath(dev.getCode());
        this.m3u8Path = H.getPath(dev.getCode(), "index.m3u8");
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
        if (!this.isRunning) {
            //如果当前已经停止，则重新运行
            this.run();
        } else {
            //如果正在运行，则重新设置startTime
            this.startTime = System.currentTimeMillis();
        }
    }

    @Override
    public void run() {
        //开始
        isRunning = true;
        //创建文件夹
        createDir();
        //运行，设置视频源和推流地址和HLS目录
        convert();
        //运行结束，从池中移除
        TaskManager.remove(dev.getCode());
        //删除文件夹
        //deleteDir();
        //结束
        isRunning = false;
    }

    private void convert() {
        String streamUrl = this.dev.getStreamUrl();
        if (StrKit.isBlank(streamUrl)) {
            return;
        }
        //检测流服务是否可访问
        if (!isConnectable(streamUrl)) {
            return;
        }
        try {
            logger.info(S.f("设备[%s]：%s", this.dev.getCode(), "开始转码"));
            Process process = F.ffmpeg(streamUrl, this.m3u8Path);
            InputStream is = process.getErrorStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            this.startTime = System.currentTimeMillis();
            while (System.currentTimeMillis() - startTime < duration && (line = br.readLine()) != null) {
                if (PropKit.getBoolean("ffmpeg.log")) {
                    logger.error(S.f("设备[%s]： %s", this.dev.getCode(), line));
                }
            }
            //关闭流，释放资源
            if (process != null) {
                process.destroy();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            logger.info(S.f("设备[%s]：%s", this.dev.getCode(), "结束转码"));
        }
    }

    /**
     * 检测指定IP+端口是否可连接
     *
     * @param streamUrl RTSP或RTMP地址
     * @return true：可连接，false：不可连接
     */
    private boolean isConnectable(String streamUrl) {
        String ip = ReUtil.get(Pattern.compile("(\\d+\\.\\d+\\.\\d+\\.\\d+):(\\d+)"), streamUrl, 1);
        String port = ReUtil.get(Pattern.compile("(\\d+\\.\\d+\\.\\d+\\.\\d+):(\\d+)"), streamUrl, 2);
        if (StrKit.isBlank(ip) || StrKit.isBlank(port)) {
            return true;
        }
        Socket socket = null;
        try {
            if (InetAddress.getByName(ip).isReachable(3000)) {
                socket = new Socket();
                socket.connect(new InetSocketAddress(ip, Integer.valueOf(port)));
                return true;
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
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
     *
     * @return true:正确 false:错误
     */
    private boolean createDir() {
        File file = new File(this.dirPath);
        if (file.exists()) return true;
        return file.mkdir();
    }

    /**
     * 删除文件夹
     *
     * @param
     */
    private void deleteDir() {
        deleteDir(this.dirPath);
    }

    private void deleteDir(String path) {
        File file = new File(path);
        //删除文件夹
        if (file.isFile()) {
            file.delete();
        } else {
            File[] files = file.listFiles();
            if (files == null) {
                file.delete();
            } else {
                for (int i = 0; i < files.length; i++) {
                    deleteDir(files[i].getAbsolutePath());
                }
                file.delete();
            }
        }
    }
}
