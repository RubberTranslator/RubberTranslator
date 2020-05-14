package com.rubbertranslator.modules.textprocessor.pre;

import com.rubbertranslator.modules.textprocessor.WordType;

/**
 * @author Raven
 * @version 1.0
 * date 2020/5/8 13:06
 * 输入给翻译引擎前的文本处理，现在的功能：
 * 1. 处理pdf复制引用的多余换行
 * 2. 给与”尽量保持分段”接口
 */
public class TextPreProcessor {

    private volatile boolean tryToKeepParagraph = true;
    private volatile boolean incrementalCopy = false;
    private final StringBuffer lastText = new StringBuffer();
    private final RedundantLineBreakProcessor redundantLineBreakProcessor = new RedundantLineBreakProcessor();


    public void cleanBuffer(){
        lastText.delete(0,lastText.length());
    }

    public void setIncrementalCopy(boolean incrementalCopy) {
        this.incrementalCopy = incrementalCopy;
        lastText.delete(0,lastText.length());
    }


    /**
     * 设置是否需要保持段落格式
     *
     * @param tryToKeepParagraph 尽量保持段落？ true 保持
     *                           false 不保持
     */
    public void setTryToKeepParagraph(boolean tryToKeepParagraph) {
        this.tryToKeepParagraph = tryToKeepParagraph;
    }

    public String process(String text) {
        if(text == null || "".equals(text)) return text;

        // 格式化
        text = tryToKeepParagraph ?
                redundantLineBreakProcessor.keepParagraphProcess(text) :
                redundantLineBreakProcessor.nonKeepParagraphProcess(text);
        // 增量复制
        if(incrementalCopy){
            if(WordType.checkType(text) ==WordType.SPACE){  // 空格系别语言
                lastText.append(text).append(" ");
            }else{
                lastText.append(text);
            }
            text = lastText.toString();
        }
        return text;
    }



}
