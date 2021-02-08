
## 0. 前言

RubberTranslator是我在使用知云文献翻译和CopyTranslator两款软件后，基于javafx开发的一款文献辅助翻译软件。总体功能思想来自CopyTranslator，在此基础之上添加了自己觉得实用的功能。

**安装：**

本项目目前支持**Windows, Linux, Mac**平台。

1. 点击[Releases](https://github.com/ravenxrz/RubberTranslator/releases)界面下载对应平台安装包即可。
2. 考虑到github国内下载速度较慢，可从[这里下载](https://ravenxrz.lanzous.com/b01bezbcf)

*注：请勿安装在有中文路径的目录下*

[点这里，看视频介绍](https://www.bilibili.com/video/BV1Wy4y1e7HG)

[此页无法查看过多gif，更多文字介绍点这里](https://ravenxrz.gitee.io/archives/a79932ef.html)

## 1. Features

1. 手动翻译

2. 有道翻译、谷歌翻译、百度翻译

3. **文本格式化，解决pdf复制的分段问题**

4. 监听剪切板翻译，选中任意文本，执行【复制】操作，自动翻译文本

5. **拖拽复制，选中任意文本，自动执行[Ctrl+C]行为，结合【监听剪切板翻译】功能，可自动翻译选中文本。**

6. 自动复制，翻译文本后，自动将【译文】放置到系统剪切板，后续只用【Ctrl+V】即可粘贴译文

7. 自动粘贴，选中任意文本，自动替换为译文。

8. **增量翻译，阅读文献时，经常会遇到一段文本分置在两页，增量问题可以自动将这段文本合并翻译**

9. **OCR翻译**

10. 翻译历史

11. **专注模式**

    <img src="https://cdn.jsdelivr.net/gh/ravenxrz/PicBed/img/image-20201224112341042.png" alt="image-20201224112341042" height="400;" />

12. **热更新**

13. **多翻译引擎对照模式**

    ![](https://cdn.jsdelivr.net/gh/ravenxrz/PicBed/img/compare.gif)

14. 过滤器，设定【拖拽复制】的过滤程序，避免在所有程序中均触发【拖拽复制】

15. **记录模式**，该模式下，会记录所有翻译文本，并导出原文、译文或双语的文档。 用于导出翻译文献非常有用。

    <img src="https://img.imgdb.cn/item/602126523ffa7d37b3abe8aa.jpg" alt="image-20210204152707436" style="zoom: 67%;" />

16. **翻译文本替换，部分专业词汇如果由翻译引擎来翻译，将显得十分晦涩难懂，使用本功能，可自定义将【晦涩的翻译词组】替换为【自己想要的词组】**

举个例子， 在数据结构或算法中，binary search 应该翻译成“二分查找”，但是如果直接使用翻译引擎翻译，将会得到二进制搜索：

此时可以添加“替换词组”：

<img src="https://cdn.jsdelivr.net/gh/ravenxrz/PicBed/img/image-20201224112619645.png" alt="image-20201224112558604" height="200;" />

最后重新翻译：

<img src="https://cdn.jsdelivr.net/gh/ravenxrz/PicBed/img/image-20201224112640280.png" alt="image-20201224112640280" height="400;" />

## 2. FAQ

使用过程中遇到问题可参考：https://docs.qq.com/doc/DT2RKQU1vb2JNRlBY

## 3. BUG 反馈或建议

1. 欢迎提issue
2. QQ群：118149802

## 4. 打包说明

打包请参见: [这里](https://github.com/ravenxrz/RubberTranslator/blob/master/RubberTranslator%E6%89%93%E5%8C%85%E8%AF%B4%E6%98%8E.md)

## 5. 请我喝杯咖啡

制作不易，如果您觉得本软件好用，也可以请我喝杯咖啡：

<table>     
    <tr>        
        <td >
            <center><img src="https://cdn.jsdelivr.net/gh/ravenxrz/PicBed/img/DXwQCn.png" width="300"><br>微信
            </center>
        </td>        
        <td >
            <center><img src="https://cdn.jsdelivr.net/gh/ravenxrz/PicBed/img/DXwQCn-1609901526157.png" width="300" ><br>支付宝
            </center>
        </td>    
    </tr>      
    <tr>         
</table>

## 开源许可

GPL v3.0
