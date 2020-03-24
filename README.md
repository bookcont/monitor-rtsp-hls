# monitor-rtmp-server 

#### 介绍
视频监控RTSP转RTMP转HLS解决方案

#### 运行流程
![输入图片说明](https://images.gitee.com/uploads/images/2020/0324/185845_f351918b_107658.png "Untitled Diagram.png")


#### 使用说明

1.  参考根目录下的nginx.conf来配置自己的web代理nginx
2.  解压nginx-rtmp-server.zip，这是作为rtmp流服务器用的nginx版本，可自行修改conf/nginx.conf配置
3.  导入monitor-rtmp至eclipse，右键Main.java运行即可

#### 感谢

1.  JFinal作者波总开发这么好用的框架，让我爱不释手
2.  参考网上整理的各主流摄像头的rtsp地址格式
3.  还有网上大神写的基于javacv将rtsp推至nginx rtmp的帖子，现在找不到了