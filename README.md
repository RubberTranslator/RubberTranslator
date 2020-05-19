Table of Contents
=================

* [0\. 前言](#0-%E5%89%8D%E8%A8%80)
* [1\. 基础功能](#1-%E5%9F%BA%E7%A1%80%E5%8A%9F%E8%83%BD)
* [2\. 高级设置](#2-%E9%AB%98%E7%BA%A7%E8%AE%BE%E7%BD%AE)
* [3\. 帮助](#3-%E5%B8%AE%E5%8A%A9)
* [4\. 常见问题](#4-%E5%B8%B8%E8%A7%81%E9%97%AE%E9%A2%98)


## 0. 前言

RubberTranslator是我在使用知云文献翻译和CopyTranslator两款软件后，基于javafx开发的一款文献辅助翻译软件。总体功能思想来自CopyTranslator，在此基础之上添加了自己觉得实用的功能。

**安装：**

本项目目前仅支持Windows平台，Linux之后会支持，但是Mac平台由于本人没有Mac电脑，所以无法支持，但是java是跨平台的，所以有mac电脑又有兴趣的朋友可自行打包。

1. 点击[Releases](https://github.com/ravenxrz/RubberTranslator/releases)界面下载对应平台安装包即可。
2. 考虑到github国内下载速度较慢，可从[这里下载](https://ravenxrz.lanzous.com/b01bezbcf)

*注：请勿安装在有中文路径的目录下*

[点这里，看视频介绍](https://www.bilibili.com/video/BV1aA411t7pY)

## 1. 基础功能

### 1.1 手动翻译

作为翻译软件最基础的功能，RubberTranslator也是支持手动翻译的，如：

![](https://cdn.jsdelivr.net/gh/ravenxrz/PicBed/img/oRsHcgsqvQ.gif)

### 1.2 翻译引擎选择&源/目标语言设置

![](https://cdn.jsdelivr.net/gh/ravenxrz/PicBed/img/S24b0bnOaP.gif)

默认支持，谷歌翻译，百度翻译和有道翻译。（百度和有道翻译需要配置API信息才可以使用，详情可参看：[ocr,百度，有道配置](https://github.com/ravenxrz/RubberTranslator/wiki/OCR%EF%BC%8C%E7%99%BE%E5%BA%A6%E5%92%8C%E6%9C%89%E9%81%93%E9%85%8D%E7%BD%AE)

### 1.3 文本格式化

文本格式化是用来做什么的呢？我们平常在阅读pdf文档的时候，经常有这样的一个问题，从pdf中拷贝的文本粘贴到其它地方会多出很多换行，如：

![](https://cdn.jsdelivr.net/gh/ravenxrz/PicBed/img/image-20200515203036559.png)

可以看到，因为多出很多空行，翻译会变得非常的不准确，一般来说，我们会手动替换掉所有的换行符，RubberTranslator默认开启”文本格式化“功能，可以用来解决问题，在替换掉换行符的同时，**尽量保持分段格式**，功能展示：

![](https://cdn.jsdelivr.net/gh/ravenxrz/PicBed/img/HpgwWgEwNd.gif)

*注意：谷歌翻译引擎不支持保持分段格式。*

### 1.4 监听剪切板

开启监听剪切板功能，只要PC剪贴板中有新文本或图片时，RubberTranslator会自动翻译，也就是说只要有”复制“(Ctrl+C或鼠标复制）动作，RubberTranslator就会复制。如：

![](https://cdn.jsdelivr.net/gh/ravenxrz/PicBed/img/iasVDY9BIQ.gif)

### 1.5 拖拽复制

每次都手动进行复制显得过于麻烦，所以拖拽复制可以实现自动复制，拖拽复制在以下两种情况下会触发：

1. 鼠标双击；
2. 鼠标点击->移动一定距离->释放。如果移动距离过近，则不会触发复制。

配置监听剪切板功能，即可实现自动翻译。演示：

双击：

![](https://cdn.jsdelivr.net/gh/ravenxrz/PicBed/img/bG2PZ7pfaF.gif)

拖拽：

![](https://cdn.jsdelivr.net/gh/ravenxrz/PicBed/img/94VVcyPHtK.gif)

### 1.6 自动复制

自动复制用于自动复制译文，RubberTranslator在翻译完一段文本后，会自动将文本放入到系统剪切板中，此时用户通过”粘贴“功能即可在任何地方输入译文了。演示：

![](https://pic.downk.cc/item/5ebf5073c2a9a83be588af19.gif)

### 1.7 自动粘贴

自动粘贴用于就地替换原文，如下：

![](https://cdn.jsdelivr.net/gh/ravenxrz/PicBed/img/mYHGWcR1eQ.gif)

### 1.8 增量复制

增量复制用于解决阅读过程中，”文本翻页“的情况，如：

![](https://cdn.jsdelivr.net/gh/ravenxrz/PicBed/img/AncXUT56Pq.gif)

### 1.9 OCR翻译

对于一些无法复制的pdf文本，可以通过ocr进行翻译，如：

![](https://cdn.jsdelivr.net/gh/ravenxrz/PicBed/img/vI26MTVGa3.gif)

ocr功能需要配置百度ocr api key。[ocr,百度，有道配置](https://github.com/ravenxrz/RubberTranslator/wiki/OCR%EF%BC%8C%E7%99%BE%E5%BA%A6%E5%92%8C%E6%9C%89%E9%81%93%E9%85%8D%E7%BD%AE)

### 1.10 历史记录

RubberTranslation也支持历史记录，默认支持10条内的记录，可在高级设置中进行修改。

### 1.11 专注模式

专注模式只保留译文，整体布局更为紧凑，适合在阅读论文时使用。

![](https://cdn.jsdelivr.net/gh/ravenxrz/PicBed/img/image-20200515211515270.png)

### 1.12 翻译接力

一直使用同一个翻译引擎时，可能会出现翻译失败的情况，在这种情况下，RubberTranslato会自动选择下一个翻译引擎进行翻译。

## 2. 高级设置

### 1. 过滤器

此功能暂时仅限Widnows平台。

过滤器用于设置不需要进行复制翻译的程序，考虑一个场景，在看论文时，我们需要在浏览器中搜寻一些资料，但是我们并不需要自动翻译浏览器中的内容，这时就可以将浏览器加入我们的过滤名单中。

操作：高级设置->过滤器,点击添加，找到浏览器的exe文件(快捷方式也可以）即可：

![](https://cdn.jsdelivr.net/gh/ravenxrz/PicBed/img/image-20200515211034440.png)

### 2. 翻译文本替换

这个功能用于将译文中的特定词组替换为自己想要的词组，可以用于替换为专有名词。举个例子，在计算机数据结构或算法上，有一个术语叫做binary search，一般中文称为二分查找，而使用翻译引擎翻译，则会被翻译为二进制搜索，这让人非常的别扭。通过”翻译文本替换“功能，我们可以还原为二分查找。

先看，没有添加词组前：

![](https://cdn.jsdelivr.net/gh/ravenxrz/PicBed/img/Gu9OkaJ3Q8.gif)

再看添加词组后：

![](https://cdn.jsdelivr.net/gh/ravenxrz/PicBed/img/MD3Q6XYcnu.gif)

### 3. 历史记录数量设置

可以设置历史记录的数量，历史记录并不会持久化到硬盘上，每次启动程序都会清空，所有历史记录都会保留在内存中，所以不建议将历史记录数量设置过大。

### 4. 自定义样式

RubberTranslator支持自定义css样式。 如，设置护眼模式，更改字体大小的css：

```css
#main {
    -fx-font-size: 10pt;
}

#focus{
}

.text-area {
	-fx-font-size: 10pt;
}

.text-area .content{
    -fx-background-color: rgb(199,237,204);
}


```

效果：

![](https://cdn.jsdelivr.net/gh/ravenxrz/PicBed/img/image-20200515212539191.png)

更多可设置效果，请参考

https://docs.oracle.com/javafx/2/api/javafx/scene/doc-files/cssref.html

### 5. OCR 百度和有道APi设置

[ocr,百度，有道配置](https://github.com/ravenxrz/RubberTranslator/wiki/OCR%EF%BC%8C%E7%99%BE%E5%BA%A6%E5%92%8C%E6%9C%89%E9%81%93%E9%85%8D%E7%BD%AE)

## 3. 常见问题

### 1. 安装后无法打开？

请确保安装路径无中文，如果安装在C盘，请给予管理员权限。

### 2. 翻译时段落识别问题，一段话被拆分为多段？

![image-20200516104744264](https://cdn.jsdelivr.net/gh/ravenxrz/PicBed/img/image-20200516104744264.png)

这和RubberTranslator在格式化复制的文本时的处理机制有关，RubberTranslator在识别多段文本的原理是，判断当前是否有 英文或中文的句号在末尾， 如果在末尾则换行。所以可以看到，原文在parameter后面有句号，RubberTranslator进行了断行。

### 3. 谷歌翻译引擎下没有分段？

是的，当前所使用的谷歌翻译引擎，会将所有文本连接成一行，所以，暂时没办法分段，后期会考虑更换接口。

### 4. OCR、百度、有道翻译引擎无效？

这三个功能需要用户自行配置App key & secret key。请参考：[ocr,百度，有道配置](https://github.com/ravenxrz/RubberTranslator/wiki/OCR%EF%BC%8C%E7%99%BE%E5%BA%A6%E5%92%8C%E6%9C%89%E9%81%93%E9%85%8D%E7%BD%AE)

## 开源许可

GPL v3.0

