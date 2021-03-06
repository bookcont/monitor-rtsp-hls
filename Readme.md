# monitor-rtsp-hls

#### 介绍
视频监控RTSP转HLS解决方案

由于公司业务，需要实现基于WEB访问监控摄像头实时流的预览，经过各种百度，补充了不少相关知识，了解到了很多大神的实现方法，也因为很多过时的帖子，而踩了不少的坑。

#### 使用说明

0.  下载源码，解压根目录中的third.zip压缩包（因为要用到nginx和ffmpeg，直接上传又超过了上传限制，所以就压缩起来），解压后项目结构如图：
    ![输入图片说明](https://images.gitee.com/uploads/images/2021/0121/114923_dd14c528_107658.png "微信图片_20210121114904.png")
1.  在项目根目录下执行mvn clean package打包，在target目录下将生成monitor-rtsp-hls-release.zip
2.  解压monitor-rtsp-hls-release.zip
3.  根据需要，修改src/main/resources/config.properties配置项，如：服务端口、服务context_path、服务域名
4.  修改conf/monitor.properties，录入项目需要对接的监控设备RTSP信息，其中监控设备代码随意命名，只是一个标识，不重复即可
5.  双击运行start.bat即可开启服务
6.  本服务提供了一个监控预览页面，在浏览器访问http://127.0.0.1:{服务端口}/{服务context_path}/live即可查看
7.  对外提供监控HLS预览地址，URL格式为http://{IP}:{服务端口}/{服务context_path}/hls/{监控设备代码}/index.m3u8

#### 注意

如果添加的监控访问不了，请将config.properties中的hls.duration配置项修改的大一些再试试！

#### 使用举例

例如现在我要接入一台监控设备，那么接入步骤就是：

0.  下载源码，解压根目录下的third.zip
1.  编译源码，在target目录下得到monitor-rtsp-hls-release.zip，解压该zip，得到monitor-rtsp-hls文件夹
2.  修改conf/monitor.properties设备配置文件，添加相应设备配置信息，如图：
![输入图片说明](https://images.gitee.com/uploads/images/2021/0121/170857_cd88f9f0_107658.png "微信图片_20210121170801.png")
3.  如果有需要修改服务端口信息，则编辑conf/config.properties中的server.port、server.context_path、server.domain等参数，没有需要不修改即可
4.  到此配置就完成了，双击根目录start.bat运行，接下来就可以打开http://127.0.0.1:{服务端口}/{服务context_path}/live地址查看配置的设备

#### 原理说明

本程序其实逻辑很简单，就是将nginx和ffmpeg整合起来，一方面将rtsp通过ffmpeg转码生成切片，一方面通过nginx将切片代理出去（大神勿喷，我只是一个搬运工）。
既然是整合，我就在程序中会去控制启动或关闭nginx和ffmpeg，这方面处理的不够好，因为是通过命令窗口启动的服务，所以不可避免的用户可能随手就关掉窗口了，但是其实后台nginx.exe和ffmpeg.exe进程还在运行，所以想了个办法就是增加了一个stop.bat脚本，需要用户手动运行，该脚本的功能就是关闭这两个进程的。
有更好办法的大神，请不吝赐教！

#### 奉上整理的几个厂家（主要是海康、大华和宇视）RTSP地址格式

[分享链接](https://mubu.com/doc/4IvOBWbQq-P)

#### 感谢

1.  JFinal作者波总开发这么好用的框架，让我爱不释手
2.  参考网上整理的各主流摄像头的rtsp地址格式
3.  还有网上大神写的基于javacv将rtsp推至nginx rtmp的帖子，现在找不到了

#### 联系方式

![输入图片说明](https://images.gitee.com/uploads/images/2021/0121/121430_5e3cfd8c_107658.jpeg "185443_1bbd3352_107658.jpg")

#### 请我吃碗螺丝粉吧

![输入图片说明](https://images.gitee.com/uploads/images/2021/0121/121217_0b892e44_107658.png "未标题-2.png")