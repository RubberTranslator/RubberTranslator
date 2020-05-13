package com.rubbertranslator.modules.textprocessor.post;

/**
 * @author Raven
 * @version 1.0
 * date 2020/5/9 9:31
 */
public class TextPostProcessor {
    // 后置处理的开关
    private volatile boolean open = true;
    // 替换器
    private WordsReplacer replacer = new WordsReplacer();

    public boolean isOpen() {
        return open;
    }

    public WordsReplacer getReplacer() {
        return replacer;
    }

    public void setReplacer(WordsReplacer replacer) {
        this.replacer = replacer;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public String process(String text){
        if(open){
            text = replacer.replace(text);
        }
        return text;
    }
}
