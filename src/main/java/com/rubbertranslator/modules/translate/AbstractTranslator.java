package com.rubbertranslator.modules.translate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Raven
 * @version 1.0
 * @date 2020/5/8 13:52
 * 所有的实际翻译引擎都应该实现
 */
public abstract class AbstractTranslator {
    
    protected Map<Language,String> langMap = new HashMap<>();

    public AbstractTranslator() {
        addLanguageMap();
    }

    public abstract void addLanguageMap();

    /**
     * 翻译
     * @param source 源语言
     * @param dest 目标语言
     * @param text 需要翻译的文本
     * @return null，如果翻译不成功
     *        翻译后的文本 成功
     *
     */
    public abstract String translate(Language source, Language dest, String text);
}
