RubberTranslator的打包需要下载两套jdk（不用更改环境变量），一是 [jdk1.8-full-version](https://bell-sw.com/pages/downloads/)， Windows和Linux还需要 [jdk14以上任意版本](https://jdk.java.net/archive/)， mac不必要。

原因有两个：

1. jdk1.8包含javafx套件，在调试开发时无需额外添加库。 虽然可以通过maven方便的添加，但是不推荐，且mac版只能使用jdk1.8才能正常打包运行。
2. 之所以下载jdk14以上版本，是因为Windows/Linux打包依赖 jpackage 工具，该工具仅在jdk14版本之上才发布。 Mac也可使用该工具打包，但是经个人测试，Mac在jdk14环境下，【拖拽复制】功能会”不灵敏“， 具体而言，是因为模拟触发【Command+C】行为容易失效，原因未知，但是jdk1.8则一切正常， mac打包通过一个maven插件来执行。那为何不放弃jpackage打包，全部平台采用maven？因为maven插件打包后的压缩高较大，jpackage只有60M左右，而maven打包后超过100M。

总结出一个表：

|          | Windows                                                      | Linux                                                   | Mac                                     |
| -------- | ------------------------------------------------------------ | ------------------------------------------------------- | --------------------------------------- |
| 开发调试 | jdk1.8                                                       | jdk1.8                                                  | jdk1.8                                  |
| 打包     | jdk14+(为了更小的压缩包)                                     | jdk14+(为了更小的压缩包)                                | jdk1.8(jdk14+会导致bug，只能使用jdk1.8) |
| 额外安装 | [javafx-jmods-11](https://gluonhq.com/products/javafx/)  \| [Wix](https://github.com/wixtoolset/wix3/releases/tag/wix3112rtm) (下载后请配置Wix的环境变量到bin目录) | [javafx-jmods-11](https://gluonhq.com/products/javafx/) | 无                                      |

Windows和Linux的完整的环境包含以下三个：

![ ](https://cdn.jsdelivr.net/gh/ravenxrz/PicBed/img/image-20210203112001128.png)

Mac只用jdk1.8即可。

## 1. Windows/Linux打包

打开IDEA:

更改jdk为1.8：

![image-20210203112130713](https://cdn.jsdelivr.net/gh/ravenxrz/PicBed/img/image-20210203112130713.png)

模块同理：

![image-20210203112434948](https://cdn.jsdelivr.net/gh/ravenxrz/PicBed/img/image-20210203112434948.png)

**确定Antifacts有两个jar包，且依赖库完整：**

![image-20210203112801312](https://cdn.jsdelivr.net/gh/ravenxrz/PicBed/img/image-20210203112801312.png)

![image-20210203112745915](https://cdn.jsdelivr.net/gh/ravenxrz/PicBed/img/image-20210203112745915.png)

然后就是正式打包了：

**step1:**

在idea中选择：Build->Build Antifacts->All antifacts.

![image-20210203113439831](https://cdn.jsdelivr.net/gh/ravenxrz/PicBed/img/image-20210203113439831.png)

**step2:**

打开对应平台脚本，package-win.bat 或者 package-linux.sh：

![image-20210203113553658](https://cdn.jsdelivr.net/gh/ravenxrz/PicBed/img/image-20210203113553658.png)

修改jpackage和jfsmoxds到相应路径，前面下载jdk14压缩包中有jpackage，javafx-jmods中有jfxmods：

Win:

![image-20210203113635045](https://cdn.jsdelivr.net/gh/ravenxrz/PicBed/img/image-20210203113635045.png)

Linux：

![image-20210203113741347](https://cdn.jsdelivr.net/gh/ravenxrz/PicBed/img/image-20210203113741347.png)

最后运行脚本即可：

打包好的文件夹在out/RubberTranslator目录下：

![image-20210203113958299](https://cdn.jsdelivr.net/gh/ravenxrz/PicBed/img/image-20210203113958299.png)

## 3. Mac打包

如前文所说，Mac使用jpackage打包后，【拖拽复制】会出现bug，所以改用了另一种打包方式。

**注意：切换到Mac分支**

给maven的settings.xml添加springio镜像（因为依赖库中有一个库只能在springio镜像中下载）：

```xml
<mirror>
	<id>springio</id>
	<mirrorOf>central</mirrorOf>
	<name>Human Readable Name for this Mirror.</name>
	<url>https://repo.spring.io/libs-snapshot/</url>
</mirror>
```

使用终端，进入Launcher目录：

```shell
mvn jfx:native
```

使用终端，进入Main目录：

```shell
mvn jfx:native
```

最后执行package-mac脚本。

打包完成，打包好的包在 Launcher/target/jfx/native 目录下。名为RubberTranslator.app.



