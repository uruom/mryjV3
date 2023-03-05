# 盲人眼镜demo使用指南

## 1.如何运行目标检测 Demo

### 环境准备

1. 在本地环境安装好 Android Studio 工具，详细安装方法请见[Android Stuido 官网](https://developer.android.com/studio)。
2. 准备一部 Android 手机，并开启 USB 调试模式。开启方法: `手机设置 -> 查找开发者选项 -> 打开开发者选项和 USB 调试模式`

**注意**：如果您的 Android Studio 尚未配置 NDK ，请根据 Android Studio 用户指南中的[安装及配置 NDK 和 CMake ](https://developer.android.com/studio/projects/install-ndk)内容，预先配置好 NDK 。您可以选择最新的 NDK 版本，或者使用
Paddle Lite 预测库版本一样的 NDK

### 部署步骤

1. 盲人眼镜所使用的目标检测 model 位于 `mryjV3/app/src/main/assets/models/picodet_xs_416_coco_lcnet_new` 目录
2. 用 Android Studio 打开 主函数`main` 工程，其运行主函数位于`mryjV3/app/src/main/java/mryj/TestMainActivity`
3. 手机连接电脑，打开 USB 调试和文件传输模式，并在 Android Studio 上连接自己的手机设备（手机需要开启允许从 USB 安装软件权限）

<p align="center">
<img src="https://paddlelite-demo.bj.bcebos.com/demo/object_detection/docs_img/android/run_app2.jpg"/>
</p>


> **注意：**
>
> > 如果您在导入项目、编译或者运行过程中遇到 NDK 配置错误的提示，请打开 ` File > Project Structure > SDK Location`，修改 `Andriod NDK location` 为您本机配置的 NDK 所在路径。
> > 如果您是通过 Andriod Studio 的 SDK Tools 下载的 NDK (见本章节"环境准备")，可以直接点击下拉框选择默认路径。
> > 还有一种 NDK 配置方法，你可以在 `picodet_detection_demo/local.properties` 文件中手动完成 NDK 路径配置，如下图所示
> > 如果以上步骤仍旧无法解决 NDK 配置错误，请尝试根据 Andriod Studio 官方文档中的[更新 Android Gradle 插件](https://developer.android.com/studio/releases/gradle-plugin?hl=zh-cn#updating-plugin)章节，尝试更新Android Gradle plugin版本。

4. 点击 Run 按钮，自动编译 APP 并安装到手机。(该过程会自动下载 Paddle Lite 预测库和模型，需要联网)。在手机运行前，可以提前打开wifi连接远程摄像头。在有连接的情况下，手机进行识别和处理的将会是远程摄像头传输的图片，反之，则是手机本身摄像头的图片。
5. 进入app，在中间会有按钮现实当前app状态，为了满足盲人需求，所有状态切换间都有语音播报。点击中间按钮后，即进行识别，对于识别出的障碍物进行方位和物体播报。

## 2.Demo 内容介绍

先整体介绍下目标检测 Demo 的代码结构之间的联系和接口，然后再分别简要的介绍 Demo 各部分功能.


1. `deepLearing`： 深度学习文件夹

```shell
# 位置：
mryjV3/app/src/main/java/deepLearning
```

2. `mryj`： 盲人眼镜主函数，架构文件夹

```shell
# 位置：
mryjV3/app/src/main/java/mryj
```

3. `musicPlay`： 音乐播放代码

```shell
# 位置：
mryjV3/app/src/main/java/musicPlay
```

4. `PictureTransmission` : 图片传递代码

```shell
# 位置：
mryjV3/app/src/main/java/picturetransmission
```

5. `Veriable`：共享内存区

```shell
# 位置
mryjV3/app/src/main/java/Veriable
```


### deepLearning

* 使用模型为 `models/picodet_xs_416_coco_lcnet_new` 
* 模型更改位置为`mryjV3/app/src/main/res/values/strings.xml` 目录下，更改`<string name="MODEL_DIR_DEFAULT">xxxx</string>`中为自己的内容

 ### mryj

 * TestMainActivity
   程序主函数，启动整个程序

  ```shell
protected void musicPlay(Estatus estatus)
 # musicPlay为初始界面的音乐播放函数
 
private void running()
# running为响应初始界面按钮，且最后有跳转函数，将会跳转至DeepLearning的MainActivity，开始deeplearning的循环
  ```

