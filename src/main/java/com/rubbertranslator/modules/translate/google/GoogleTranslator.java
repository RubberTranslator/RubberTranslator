package com.rubbertranslator.modules.translate.google;

import com.rubbertranslator.modules.translate.AbstractTranslator;
import com.rubbertranslator.modules.translate.Language;
import com.rubbertranslator.utils.OkHttpUtil;
import okhttp3.Request;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Raven
 * @version 1.0
 * date 2020/5/8 17:17
 */
public class GoogleTranslator extends AbstractTranslator {

    // 采用正则表达式从html中抽取翻译结果--可能会失效
    private final Pattern translationPattern = Pattern.compile("<div dir=\"ltr\" class=\"t0\">(.*?)</div>");

    //https://translate.google.com/m?sl=auto&tl=zh-TW&hl=en&mui=tl
    @Override
    public void addLanguageMap() {
        langMap.put(Language.AUTO, "auto");
        langMap.put(Language.CHINESE_SIMPLIFIED, "zh-CH");
        langMap.put(Language.CHINESE_TRADITIONAL, "zh-TW");
        langMap.put(Language.ENGLISH, "en");
        langMap.put(Language.FRENCH, "fr");
        langMap.put(Language.JAPANESE, "ja");
    }

    /**
     * @param source 源语言
     * @param dest   目标语言
     * @param text   需要翻译的文本
     * @return 成功，翻译后的文本
     *          失败,null
     */
    @Override
    public String translate(Language source, Language dest, String text) {
        String translatedText = null;
        try {
            String html = doTranslate(langMap.get(source), langMap.get(dest), text);
            if (html != null) {
                translatedText = extractTranslation(html);
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, translatedText);
            }
        } catch (IOException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Google翻译失败", e);
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getLocalizedMessage(), e);
        }
        return translatedText;
    }

    /**
     * 谷歌翻译，请求翻译网页
     *
     * @param source 源语言
     * @param dest   目标语言
     * @param text   html
     * @return 成功，html
     * 失败 null
     */
    private String doTranslate(String source, String dest, String text) throws IOException {
        String result = null;
        try{
            String pageUrl = String.format("https://translate.google.cn/m?sl=%s&tl=%s&q=%s",
                    source, dest, URLEncoder.encode(text, StandardCharsets.UTF_8));
            Request request = new Request.Builder()
                    .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36")
                    .url(pageUrl)
                    .get()
                    .build();
            result = OkHttpUtil.syncRequest(request);
        }catch (SocketException | SocketTimeoutException e){
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING,e.getLocalizedMessage(),e);
        }
        return result;
    }

    /**
     * 从html中抽取翻译后的文本
     *
     * @param html
     * @return
     */
    private String extractTranslation(String html) {
        Matcher matcher = translationPattern.matcher(html);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
