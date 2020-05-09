package com.rubbertranslator.modules.textprocessor.pre;

/**
 * @author Raven
 * @version 1.0
 * @date 2020/5/8 13:06
 * 输入给翻译引擎前的文本处理，现在的功能：
 * 1. 处理pdf复制引用的多余换行
 * 2. 给与”尽量保持分段”接口
 */
public class TextPreProcessor {

    private volatile boolean tryToKeepParagraph = true;
    private final RedundantLineBreakProcessor redundantLineBreakProcessor = new RedundantLineBreakProcessor();

    /**
     * 设置是否需要保持段落格式
     *
     * @param tryToKeepParagraph 尽量保持段落？ true 保持
     *                           false 不保持
     */
    public void tryToKeepParagraph(boolean tryToKeepParagraph) {
        this.tryToKeepParagraph = tryToKeepParagraph;
    }

    public String process(String text) {
        return tryToKeepParagraph ?
                redundantLineBreakProcessor.keepParagraphProcess(text) :
                redundantLineBreakProcessor.nonKeepParagraphProcess(text);
    }

}
