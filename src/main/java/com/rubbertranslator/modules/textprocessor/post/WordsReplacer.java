package com.rubbertranslator.modules.textprocessor.post;

import com.rubbertranslator.modules.textprocessor.WordType;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author Raven
 * @version 1.0
 * date 2020/5/9 9:33
 * 词组替换器
 */
public class WordsReplacer {
    private Set<WordsPair> wordsPairs = new HashSet<>();
    // 区分大小写与否
    private boolean caseInsensitive = true;
    // 是否开启替换
    private boolean openWordsReplacer = true;

    public WordsReplacer() {
    }

    public WordsReplacer(Set<WordsPair> wordsPairs, boolean caseInsensitive, boolean openWordsReplacer) {
        this.wordsPairs = wordsPairs;
        this.caseInsensitive = caseInsensitive;
        this.openWordsReplacer = openWordsReplacer;
    }

    public boolean isCaseInsensitive() {
        return caseInsensitive;
    }

    public Set<WordsPair> getWordsPairs() {
        return wordsPairs;
    }

    public void setWordsPairs(Set<WordsPair> wordsPairs) {
        this.wordsPairs = wordsPairs;
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

    public void addWord(WordsPair wordsPair) {
        wordsPairs.add(wordsPair);
    }

    public void addWords(Collection<WordsPair> words) {
        if (words == null) return;
        words.removeIf(Objects::isNull);
        wordsPairs.addAll(words);
    }
    // set
    public String replace(String text) {
        if (!openWordsReplacer) return text;
        String src, dest;
        for (WordsPair wordsPair : wordsPairs) {
            src = wordsPair.getFirst();
            dest = wordsPair.getSecond();
            text = doReplace(src, dest, text);
        }
        return text;
    }

    private String doReplace(String src, String dest, String text) {
        // 检查时哪种类型的词组
        String result;
        WordType wordType = WordType.checkType(text);
        if (wordType == WordType.SPACE) {
            src = "\\b" + src + "\\b";  // 单词边界
        }
        if (!caseInsensitive) {
            result = text.replaceAll("(?i)" + src, dest);
        } else {
            result = text.replaceAll(src, dest);
        }
        return result;
    }

}
