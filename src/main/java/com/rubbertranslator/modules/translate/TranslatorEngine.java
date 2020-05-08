package com.rubbertranslator.modules.translate;

/**
 * @author Raven
 * @version 1.0
 * @date 2020/5/8 13:52
 * 所有的实际翻译引擎都应该实现这个接口
 */
public interface TranslatorEngine {
    /**
     * 翻译
     * @param source 源语言
     * @param dest 目标语言
     * @param text 需要翻译的文本
     * @return null，如果翻译不成功
     *        翻译后的文本 成功
     *
     */
    String translate(String source, String dest, String text);
}
