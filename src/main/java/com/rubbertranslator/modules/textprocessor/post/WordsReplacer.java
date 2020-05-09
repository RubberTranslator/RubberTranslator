package com.rubbertranslator.modules.textprocessor.post;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Raven
 * @version 1.0
 * @date 2020/5/9 9:33
 * 词组替换器
 */
public class WordsReplacer {
    // TODO: 添加词组替换开关（如果有多个后置文本处理器的话）
    private Map<String, String> map = new HashMap<>();
    // 区分大小写与否
    private boolean caseInsensitive = true;

    public boolean isCaseInsensitive() {
        return caseInsensitive;
    }

    public void setCaseInsensitive(boolean caseInsensitive) {
        this.caseInsensitive = caseInsensitive;
    }

    public void addWord(String src, String dest) {
        map.put(src, dest);
    }

    public void addWords(Map<String, String> words) {
        map.putAll(words);
    }

    public String replace(String text) {
        String src, dest;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            src = entry.getKey();
            dest = entry.getValue();
            text = doReplace(src, dest, text);
        }
        return text;
    }

    private String doReplace(String src, String dest, String text) {
        // 检查时哪种类型的词组
        String result = text;
        WordType wordType = checkWordType(text);
        if (wordType == WordType.SPACE) {
            src = "\b"+src + "\b";
        }
        if (!caseInsensitive) {
            result = text.replaceAll("(?i)" + src , dest);
        } else {
            result = text.replaceAll( src , dest);
        }
        return result;
    }

    // XXX: 目前仅支译文为非空格类型的语言词组，如中文、日文等
    private WordType checkWordType(String text) {
        return WordType.UNSPACE;
    }

    private enum WordType {
        SPACE,  // 单词与单词之间有空格，如英语
        UNSPACE // 单词与单词之间没有空格，如中文
    }
}
