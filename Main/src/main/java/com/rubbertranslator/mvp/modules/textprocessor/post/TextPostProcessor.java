package com.rubbertranslator.mvp.modules.textprocessor.post;

/**
 * @author Raven
 * @version 1.0
 * date 2020/5/9 9:31
 */
public class TextPostProcessor {

    // 替换器
    private WordsReplacer replacer = new WordsReplacer();
    // 段落自动缩进器
    private ParagraphIndentProcessor indentProcessor = new ParagraphIndentProcessor();

    public WordsReplacer getReplacer() {
        return replacer;
    }

    public void setReplacer(WordsReplacer replacer) {
        this.replacer = replacer;
    }


    /**
     * 文本后期处理
     * @param origin
     * @param translation
     * @return  failed: null
     */
    public String process(String origin,String translation){
        // 译文词组替换
        translation = replacer.replace(translation);
        // 段落增加缩进（有换行符则缩进）
        translation = indentProcessor.process(origin,translation);
        return translation;
    }
}
