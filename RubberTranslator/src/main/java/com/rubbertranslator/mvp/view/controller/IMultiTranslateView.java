package com.rubbertranslator.mvp.view.controller;

import com.rubbertranslator.enumtype.TranslatorType;

public interface IMultiTranslateView extends ITranslateView{
    /**
     * 清除所有文本
     */
    void clearAllText();

    /**
     * 设置翻译译文
     * @param type
     * @param translatedText
     */
    void setTranslateResult(TranslatorType type,String translatedText);
}
