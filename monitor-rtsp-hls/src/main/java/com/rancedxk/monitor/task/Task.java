package com.rancedxk.monitor.task;

import static org.bytedeco.ffmpeg.global.avcodec.av_packet_unref;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

import org.bytedeco.ffmpeg.avcodec.AVPacket;
import org.bytedeco.ffmpeg.avformat.AVFormatContext;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.FrameGrabber.Exception;

import com.jfinal.kit.Kv;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.template.Engine;

import cn.hutool.core.util.ReUtil;
import com.rancedxk.monitor.task.DevManager.Dev;

public class Task implements Runnable{
	private boolean isRunning = false;
	//记录线程开始时间
	private long startTime;
	//转换可持续最大时间，达到时间则自动结束
	private static final long duration = PropKit.getLong("rtmp.duration");
	//设备信息
	private Dev dev;
	//推流地址
	private String rtmp;
	
	public Task(Dev dev) {
		this.dev = dev;
		this.rtmp = Engine.use().getTemplateByString(PropKit.get("rtmp.path")).renderToString(Kv.by("code", dev.getCode()));
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
		//运行，设置视频源和推流地址
    	convert(dev.getRtsp(), rtmp);
    	//运行结束，从池中移除
	    TaskManager.remove(dev.getCode());
	    //结束
	    isRunning = false;
	}
	
	@SuppressWarnings("resource")
	private void convert(String rtsp, String rtmp) {
		//检测rtsp流服务是否可访问
		if(!isConnectable(rtsp)){
			return;
		}
		
		FFmpegFrameGrabber grabber = null;
		FFmpegFrameRecorder record = null;
		try {
			//1.输入
			//采集/抓取器
		    grabber = new FFmpegFrameGrabber(rtsp);
	        grabber.setOption("rtsp_transport","tcp");
	        //开始之后ffmpeg会采集视频信息，之后就可以获取音视频信息
	        grabber.start();
	        int width = grabber.getImageWidth();
	        int height = grabber.getImageHeight();
	        //视频参数
	//        int audioCodec = grabber.getAudioCodec();
	//        String audioCodecName = grabber.getAudioCodecName();
	        int codecid = grabber.getVideoCodec();
	        double framerate = grabber.getVideoFrameRate();// 帧率
	        int bitrate = grabber.getVideoBitrate();// 比特率
	        //音频参数
	        //想要录制音频，这三个参数必须有：audioChannels > 0 && audioBitrate > 0 && sampleRate > 0
	        int audioChannels = grabber.getAudioChannels();
	        int audioBitrate = grabber.getAudioBitrate();
	        if (audioBitrate < 1) {
	            audioBitrate = 128 * 1000;// 默认音频比特率
	        }
	        
	        //2.输出
	        //录制/推流器
	        record = new FFmpegFrameRecorder(rtmp, width, height);
	        record.setVideoOption("crf", "18");
	        record.setGopSize(2);
	        record.setFrameRate(framerate);
	        record.setVideoBitrate(bitrate);
	        record.setAudioChannels(audioChannels);
	        record.setAudioBitrate(audioBitrate);
	        record.setSampleRate(0);
	        AVFormatContext fc = null;
	        //封装格式flv
	        record.setFormat("flv");
	//        record.setAudioCodec(audioCodec);
	//        record.setAudioCodecName(audioCodecName);
	        record.setAudioCodecName("aac");
	        record.setVideoCodec(codecid);
	        fc = grabber.getFormatContext();
	        record.start(fc);
	        
	        //3.开始转换
			this.startTime = System.currentTimeMillis();
	        //采集或推流导致的错误次数
	        long err_index = 0;
	        //连续五次没有采集到帧则认为视频采集结束，程序错误次数超过1次即中断程序
	        for(int no_frame_index=0;no_frame_index<5||err_index>1;) {
	        	//达到转换持续最大时间则中止
	        	if(System.currentTimeMillis()-startTime>=duration){
	        		break;
	        	}
	            AVPacket pkt=null;
	            try {
	                //没有解码的音视频帧
	                pkt=grabber.grabPacket();
	                if(pkt==null||pkt.size()<=0||pkt.data()==null) {
	                    //空包记录次数跳过
	                    no_frame_index++;
	                    continue;
	                }
	                //不需要编码直接把音视频帧推出去
	                err_index+=(record.recordPacket(pkt)?0:1);//如果失败err_index自增1
	                av_packet_unref(pkt);
	            }catch (Exception e) {//推流失败
	                err_index++;
	            } catch (IOException e) {
	                err_index++;
	            }
	        }
		} catch (Exception e1) {
			e1.printStackTrace();
		} catch (org.bytedeco.javacv.FrameRecorder.Exception e2) {
			e2.printStackTrace();
		} finally {
			if(grabber!=null){
				try {
					grabber.stop();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(record!=null){
				try {
					record.stop();
				} catch (org.bytedeco.javacv.FrameRecorder.Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 检测指定IP+端口是否可连接
	 * @param host
	 * @param port
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
}
