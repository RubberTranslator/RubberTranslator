package com.rubbertranslator.modules.textprocessor.post;

import com.rubbertranslator.modules.textprocessor.WordType;

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
    // 是否开启替换
    private boolean openWordsReplacer = true;

    public boolean isCaseInsensitive() {
        return caseInsensitive;
    }

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }

    public boolean isOpenWordsReplacer() {
        return openWordsReplacer;
    }

    public void setOpenWordsReplacer(boolean openWordsReplacer) {
        this.openWordsReplacer = openWordsReplacer;
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
        if(!openWordsReplacer) return text;
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
        WordType wordType = WordType.checkType(text);
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

}
