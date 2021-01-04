package com.rubbertranslator.mvp.view.controller;

/**
 * view 接口
 * mainView
 */
public interface ISingleTranslateView extends ITranslateView {

    /**
     * 设置翻译文本
     * @param originText 原文
     * @param translatedText 译文
     */
    default void setText(String originText, String translatedText){}


    /**
     * 下面两个接口，可修复，两个的功能，主要是bind copy &　paste动作，因为paste依赖copy
     * @param isOpen 是否打开，true开头，false，关闭
     */
    default void autoCopy(boolean isOpen){}

    default void autoPaste(boolean isOpen){}
}
