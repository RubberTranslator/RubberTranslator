package com.rubbertranslator.modules.textinput;

import com.rubbertranslator.manager.TranslatorFacade;
import com.rubbertranslator.modules.textinput.ocr.OCRUtils;

import java.awt.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Raven
 * @version 1.0
 * @date 2020/5/8 9:51
 * 汇集所有输入文本，包含：
 * 1. 监听剪切板新内容
 * 2. 用户从UI输入
 */
public class TextInputCollector implements TextInputListener {
    //xxx: 是否需要加入输入队列，然后加入翻译完成判断回调？都采用同步请求，似乎又没有必要
    private TranslatorFacade facade;

    public TextInputCollector(TranslatorFacade facade) {
        this.facade = facade;
    }

    public void setFacade(TranslatorFacade facade) {
        this.facade = facade;
    }

    @Override
    public void onTextInput(String text) {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, text);
        facade.process(text);
    }

    @Override
    public void onImageInput(Image image) {
        try {
            String text = OCRUtils.ocr(image);
            facade.process(text);
        } catch (IOException e) {
            e.printStackTrace();
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
