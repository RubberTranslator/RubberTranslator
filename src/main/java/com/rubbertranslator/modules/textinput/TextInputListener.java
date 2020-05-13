package com.rubbertranslator.modules.textinput;

import java.awt.*;

/**
 * @author Raven
 * @version 1.0
 * date 2020/5/8 9:52
 */
public interface TextInputListener {
    /**
     * 文本输入时
     *
     * @param text 输入的文本
     */
    void onTextInput(String text);

    /**
     * 图片输入时
     *
     * @param image 输入的图像
     */
    void onImageInput(Image image);
}
