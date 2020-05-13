package com.rubbertranslator.modules.textprocessor.pre;

/**
 * @author Raven
 * @version 1.0
 * date 2020/5/8 13:08
 * 多余空行处理
 */
public class RedundantLineBreakProcessor {

    public String nonKeepParagraphProcess(String text) {
        // 或者改用正则表达式来做
        String[] splitText = text.split("\n");
        StringBuilder sb = new StringBuilder();
        for (String tempStr : splitText) {
            if(tempStr == null || tempStr.equals("")) continue;
            if (tempStr.charAt(tempStr.length() - 1) == '-') {  // 英文末尾的连接符
                sb.append(tempStr, 0, tempStr.length() - 1);
            } else {
                sb.append(tempStr).append(" "); // 非-结尾单词，是完整单词，需要添加空格，和后文单词分割开
            }
        }
        return sb.toString();
    }

    public String keepParagraphProcess(String text) {
        String[] splitText = text.split("\n");
        StringBuilder sb = new StringBuilder();
        for (String tempStr : splitText) {
            // 有些空行
            if(tempStr == null || tempStr.equals("")) continue;
            char lastChar = tempStr.charAt(tempStr.length() - 1);
            if (lastChar == '.' || lastChar == '。') {  // 最后字符是. 也就是说.后又是换行，大概率是分段
                sb.append(tempStr).append("\n");
            } else if (tempStr.charAt(tempStr.length() - 1) == '-') {  // 英文末尾的连接符
                sb.append(tempStr, 0, tempStr.length() - 1);
            } else {
                sb.append(tempStr).append(" ");
            }
        }
        return sb.toString();
    }

}
