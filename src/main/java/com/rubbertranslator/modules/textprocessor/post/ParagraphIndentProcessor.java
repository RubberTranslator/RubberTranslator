package com.rubbertranslator.modules.textprocessor.post;

/**
 * @author Raven
 * @version 1.0
 * date  2020/5/20 9:29
 * 为段落增加换行
 */
public class ParagraphIndentProcessor {

    private boolean autoIndent = true;

    public boolean isAutoIndent() {
        return autoIndent;
    }

    public void setAutoIndent(boolean autoIndent) {
        this.autoIndent = autoIndent;
    }

    /**
     * 为translation中的段落增加两个字符的缩进,
     * new缩进要求：检查原文中是否有换行符
     *
     * 过时：old缩进要求:原文单词量大于等于3个（英文系）
     *         原文长度大于10
     * @param origin 原文，用于辅助判断是否需要处理
     * @param text 要处理的文本
     * @return 增加了缩进的文本
     */
    public String process(String origin, String text){
        if(!autoIndent) return  text;
        if(!origin.contains("\n")) return text;

//        if(origin.split(" +").length <=3){
//            return translation;
//        }
//        if(origin.length() <= 10){
//            return translation;
//        }

        StringBuilder sb = new StringBuilder();
        String[] split = text.split("\n");
        for(String p: split){
            sb.append("\t").append(p).append("\n");
        }
        // 删除最后的\n
        sb.delete(sb.length()-1,sb.length());
        return sb.toString();
    }
}
