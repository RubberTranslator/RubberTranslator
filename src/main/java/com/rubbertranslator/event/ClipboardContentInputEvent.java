package com.rubbertranslator.event;

import java.awt.*;

/**
 * @author Raven
 * @version 1.0
 * date  2020/5/20 22:11
 * 剪切板内容输入监听
 */
public class ClipboardContentInputEvent {
    private boolean isTextType;
    private String text;
    private Image image;

    public boolean isTextType() {
        return isTextType;
    }

    public void setText(String text){
        isTextType = true;
        this.text = text;
    }

    public void setImage(Image image) {
        isTextType = false;
        this.image = image;
    }

    public String getText() {
        return text;
    }

    public Image getImage() {
        return image;
    }
}
