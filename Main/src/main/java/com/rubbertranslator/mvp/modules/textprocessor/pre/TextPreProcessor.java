package com.rubbertranslator.mvp.modules.textprocessor.pre;

import com.rubbertranslator.enumtype.TranslatorType;
import com.rubbertranslator.enumtype.WordType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Raven
 * @version 1.0
 * date 2020/5/8 13:06
 * 输入给翻译引擎前的文本处理，现在的功能：
 * 1. 处理pdf复制引用的多余换行
 * 2. 给与”尽量保持分段”接口
 */
public class TextPreProcessor {

    private boolean tryToFormat = true;

    private boolean incrementalCopy = false;

    private final StringBuffer lastText = new StringBuffer();

    private final Map<TranslatorType, StringBuffer> lastTextTypeMap = new HashMap<>();

    private final RedundantLineBreakProcessor redundantLineBreakProcessor = new RedundantLineBreakProcessor();

    public TextPreProcessor() {
        lastTextTypeMap.put(TranslatorType.GOOGLE, new StringBuffer());
        lastTextTypeMap.put(TranslatorType.BAIDU, new StringBuffer());
        lastTextTypeMap.put(TranslatorType.YOUDAO, new StringBuffer());
    }

    public void cleanBuffer() {
        lastText.delete(0, lastText.length());
        for (Map.Entry<TranslatorType, StringBuffer> entry : lastTextTypeMap.entrySet()) {
            entry.getValue().delete(0, entry.getValue().length());
        }
    }

    public void setIncrementalCopy(boolean incrementalCopy) {
        this.incrementalCopy = incrementalCopy;
        cleanBuffer();
    }


    /**
     * 设置是否需要保持段落格式
     *
     * @param tryToFormat 尽量保持段落？ true 保持
     *                    false 不保持
     */
    public void setTryToFormat(boolean tryToFormat) {
        this.tryToFormat = tryToFormat;
    }

    public String process(String text) {
        if (text == null || "".equals(text)) return text;

        // 格式化
        text = tryToFormat ?
                redundantLineBreakProcessor.format(text) :
                text;
        // 增量复制
        if (incrementalCopy) {
            if (WordType.checkType(text) == WordType.SPACE) {  // 空格系别语言
                lastText.append(text).append(" ");
            } else {
                lastText.append(text);
            }
            text = lastText.toString();
        }
        return text;
    }

    public String processWithType(String text, TranslatorType type) {
        if (text == null || "".equals(text)) return text;

        // 格式化
        text = tryToFormat ?
                redundantLineBreakProcessor.format(text) :
                text;

        //  取出对应type的lastText
        StringBuffer lastText = lastTextTypeMap.get(type);
        assert (lastText != null);

        // 增量复制
        if (incrementalCopy) {
            if (WordType.checkType(text) == WordType.SPACE) {  // 空格系别语言
                lastText.append(text).append(" ");
            } else {
                lastText.append(text);
            }
            text = lastText.toString();
        }
        return text;
    }


}
