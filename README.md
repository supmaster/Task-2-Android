[![GitHub license](https://img.shields.io/badge/license-MIT-blue.svg)](https://raw.githubusercontent.com/modood/Administrative-divisions-of-China/master/LICENSE)
[![Build Status](https://travis-ci.org/xialonghua/kotmvp.svg?branch=master)](https://travis-ci.org/supmaster/Creative-Challenge-MQTT-BugList) 
![Github stars](https://img.shields.io/github/stars/supmaster/Task-2-Android.svg)

# Task-2-Android
## 1 概述
基于环信MQTT开发的实时图表Android版，实现多终端互动
<br />
<img src="https://github.com/supmaster/Task-2-Android/blob/main/Screenshots/home.jpg" width="auto" height="700px" alt="登录成功"/><br/>
### 1.1 视频演示
#### 1.1.1 柱状条投票
点击柱状条，可以看到票数增加（该动作仅做演示暂时没有做MQTT同步）
<br /><img src="https://github.com/supmaster/Task-2-Android/blob/main/Screenshots/clickBars.gif" width="auto" height="600px" alt="点击柱状条"/><br/>
#### 1.1.2 多设备协同投票
两部手机均安装该demo，同时打开，可以看到上方显示不同的用户名，分别点击按钮，可以看到本机和对方数据均随之变化。
> * 发起方和接受方数据几乎同时变化，是因为逻辑上发起方仅做了推送MQTT消息的动作，大家收到消息一起响应
> * 视频来看延时将近1秒，是因为左边手机用右边的热点，而月底右边的手机由于流量超了网速已经被限制到接近2G，连WiFi或4G网络会很快

<video src="https://github.com/supmaster/Task-2-Android/blob/main/Screenshots/clickButtonsV3.gif" controls="controls" width="500" height="300">您的浏览器不支持播放该视频！</video>

## 2 功能介绍
### 2.1 系统功能
#### 2.1.1 配置说明
* 配置文件在MainActivity.java中，同一协作团队MQTT配置须一致
* 生成apk后，设备须在**联网环境**下使用
#### 2.1.2 运行应用
* 应用启动时可以从Logcat看到MQTT相关日志：
  * MQTT客户端初始化
  * 获取token
  * 用户注册（已注册用户提示注册失败，不影响）
  * 用户登录
  * MQTT客户端连接云端
* 启动成功后界面如下：
### 2.2 业务功能
#### 2.2.1 点击柱状条
* 可以直接点击响应的柱状条给其投票
* 每点击一次增加一票
<br /><img src="https://github.com/supmaster/Task-2-Android/blob/main/Screenshots/clickBar.jpg" width="auto" height="600px" alt="点击柱状条"/><br/>
#### 2.2.2 点击按钮“JAVA”
* 点击按钮“支持Java”
* 可以看到Java在图表中的Bar增加1
* 同时推送到其他用户
* 其他用户收到消息更新自己图表
<br /><img src="https://github.com/supmaster/Task-2-Android/blob/main/Screenshots/clickBt1.jpg" width="auto" height="600px" alt="支持Java"/><br/>
#### 2.2.2 点击按钮“JS”
* 点击按钮“支持JS”
* 可以看到JS在图表中的Bar增加1
* 同时推送到其他用户
* 其他用户收到消息更新自己图表
<br /><img src="https://github.com/supmaster/Task-2-Android/blob/main/Screenshots/clickBt2.jpg" width="auto" height="600px" alt="支持JS"/><br/>
#### 2.2.4 点击按钮“重置”
* 点击按钮“重置”
* 可以看到图表中的数据都归零
* 同时推送到其他用户
* 其他用户收到消息更新自己图表
<br /><img src="https://github.com/supmaster/Task-2-Android/blob/main/Screenshots/reset.jpg" width="auto" height="600px" alt="重置"/><br/>

<!-- ROADMAP -->
## 3 技术组件
- [x] MQTT
- [x] AAChart
- [x] okhttp3

<!-- GETTING STARTED -->
## 4 快速上手

1. 克隆代码 (`git clone https://github.com/supmaster/Task-2-Android.git`)
2. 本地准备好Android开发环境，如jdk、sdk等
3. 用Android Studio打开项目
4. 执行Gradle Sync
5. 运行项目
> 注意：须在MainActivity.java中补充MQTT相关配置

<!-- LICENSE -->
## 5 开源协议

基于 MIT 开源协议. 点击 `LICENSE` 查看更多信息

<!-- CONTACT -->
## 6 联系作者

Supmaster - [@github_handle](https://github.com/supmaster) - email

Project Link: [https://github.com/supmaster/Task-2-Android](https://github.com/supmaster/Task-2-Android)

<!-- ACKNOWLEDGEMENTS -->
## 7 致谢

- [x] [环信](https://console.easemob.com)
- [x] [IM Geek]()
- [x] []()社群小姐姐 :girl:

[回到顶部](#Task-2-Android)
