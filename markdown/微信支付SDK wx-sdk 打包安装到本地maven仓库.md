---
created: 2022-02-18T03:27:44 (UTC +08:00)
tags: [MAVEN,SDK,DEPENDENCY,TARGET,自己动手,只不过,HTTPS]
source: https://www.bilibili.com/read/cv10143835/
author: 芯_t
---

# 微信支付SDK wx-sdk 打包安装到本地maven仓库 - 哔哩哔哩

> ## Excerpt
> 官方SDK为3.0.9版本同样也是基于maven构建，只不过没有上传到maven的公共仓库，那么我们就可以自己动手打成jar包从而导入本地maven项目。1 下载官方sdk项目官方 https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=11_12 导入idea3 给WXPayConfig.java 中抽象方法加上public修饰符4 进行 mav install ，在target 目录下找到wxpay-sdk-**.jar5 进入cmd

---
官方SDK为3.0.9版本同样也是基于maven构建，只不过没有上传到maven的公共仓库，那么我们就可以自己动手打成jar包从而导入本地maven项目。

1 下载官方sdk项目

官方 https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=11\_1

![](Markdown_Yong_JPG/0bcaa0577bcc509a1b0d455a8bbc955e13b90c3a.png@942w_285h_progressive.webp)

2 导入idea

![](Markdown_Yong_JPG/c41d3c7f9e720a14d6d95ce11355a6fed6049250.png@942w_590h_progressive.webp)

3 给WXPayConfig.java 中抽象方法加上public修饰符

![](Markdown_Yong_JPG/06b06a66a58002e366ca3359c8092963fcf23f59.png@942w_590h_progressive.webp)

![](Markdown_Yong_JPG/b1266b8a8ea6a40093be4b53411b7b3a00cc0a4b.png@942w_590h_progressive.webp)

4 进行 mav install ，在target 目录下找到wxpay-sdk-\*\*.jar

![](Markdown_Yong_JPG/a75c030db5d421741d3d9096d8c3d39a1151b5a9.png@942w_590h_progressive.webp)

![](Markdown_Yong_JPG/1bf918280b101254b5beaaf45ee716539eb40168.png@942w_590h_progressive.webp)

5 进入cmd 

![](Markdown_Yong_JPG/de273df1a280aed08498c39faa9ff89f76d3304f.png@942w_494h_progressive.webp)

6 运行7的 mvn install:install-file -Dfile=D:\\wxpay-sdk-3.0.9.jar -DgroupId=com.github.wxpay \-DartifactId=wxpay-sdk -Dversion=3.0.9 -Dpackaging=jar 命令。

7 安装本地jar包到本地仓库需要如下命令(注意不要换行):   

mvn install:install-file -Dfile=D:\\wxpay-sdk-3.0.9.jar -DgroupId=com.github.wxpay \-DartifactId=wxpay-sdk -Dversion=3.0.9 -Dpackaging=jar

参数说明

mvn install:install-file -Dfile=<path-to-file> -DgroupId=<group-id> -DartifactId=<artifact-id> -Dversion=<version> -Dpackaging=<packaging> 

<path-to-file>: 要安装的JAR的本地路径

<group-id>：要安装的JAR的Group Id

<artifact-id>: 要安装的JAR的 Artificial Id

<version>: JAR 版本 <packaging>: 打包类型，例如JAR

8  maven的pom.xml引入wxpay-sdk依赖。

 <dependency>

            <groupId>com.github.wxpay</groupId>

            <artifactId>wxpay-sdk</artifactId>

            <version>3.0.9</version>

        </dependency>

![](Markdown_Yong_JPG/25fc6a221412c55c9968b45a64e4ed7d4bd5c908.png@942w_590h_progressive.webp)
