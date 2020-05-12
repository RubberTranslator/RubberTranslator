package com.rubbertranslator.modules.textprocessor;

public enum WordType {
        SPACE,  // 单词与单词之间有空格，如英语
        UNSPACE; // 单词与单词之间没有空格，如中文

    /**
     * //TODO：检测语言是什么系别的语言
     * @param text
     * @return
     */
    public static WordType checkType(String text){
            return UNSPACE;
    }
}