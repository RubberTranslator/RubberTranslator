package com.rubbertranslator.modules.translate.google;

import com.rubbertranslator.modules.translate.ITranslator;
import com.rubbertranslator.utils.OkHttpUtil;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Raven
 * @version 1.0
 * @date 2020/5/8 17:17
 */
public class GoogleTranslator implements ITranslator {

    // 采用正则表达式从html中抽取翻译结果--可能会失效
    private final Pattern translationPattern = Pattern.compile("<div dir=\"ltr\" class=\"t0\">(.*?)</div>");

    /**
     *
     * @param source 源语言
     * @param dest 目标语言
     * @param text 需要翻译的文本
     * @return
     */
    @Override
    public String translate(String source, String dest, String text) {
        String translatedText = null;
        try {
            String html = doTranslate(source,dest,text);
            Logger.getLogger(this.getClass().getName()).info(html);
            if(html != null){
                translatedText = extractTranslation(html);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,e.getMessage(),e);
        }
        Logger.getLogger(this.getClass().getName()).info("Google："+translatedText);
        return translatedText;
    }

    /**
     * 谷歌翻译，请求翻译网页
     * @param source 源语言
     * @param dest 目标语言
     * @param text html
     * @return 成功，html
     *         失败 null
     */
    private String doTranslate(String source, String dest, String text) throws IOException {
        String pageUrl = String.format("https://translate.google.com/m?sl=%s&tl=%s&q=%s",
                source, dest, URLEncoder.encode(text, StandardCharsets.UTF_8));
        Request request = new Request.Builder()
                .addHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11")
                .url(pageUrl)
                .get()
                .build();
        return OkHttpUtil.syncRequest(request);
    }

    /**
     * 从html中抽取翻译后的文本
     * @param html
     * @return
     */
    private String extractTranslation(String html){
        Matcher matcher = translationPattern.matcher(html);
        if(matcher.find()){
            return matcher.group(1);
        }
        return null;
    }
}
