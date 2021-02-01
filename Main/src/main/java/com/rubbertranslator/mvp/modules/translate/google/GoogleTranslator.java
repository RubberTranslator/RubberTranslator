package com.rubbertranslator.mvp.modules.translate.google;

import com.rubbertranslator.enumtype.Language;
import com.rubbertranslator.mvp.modules.translate.AbstractTranslator;
import com.rubbertranslator.utils.OkHttpUtil;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
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

    private String patternUrl = "https://cdn.jsdelivr.net/gh/ravenxrz/RubberTranslator@latest/Main/misc/google-api-pattern.txt";

    {
        try {
            Properties props = new Properties();
            props.load(this.getClass().getResourceAsStream("/config/misc.properties"));
            patternUrl = props.getProperty("google-api-pattern-url");
        } catch (IOException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Goolge翻译初始化远端pattern url失败");
        }
    }


    // 采用正则表达式从html中抽取翻译结果--可能会失效
    private volatile String patternStr = "<div class=\"result-container\">([\\s\\S]*?)</div>";

    private volatile Pattern translationPattern = Pattern.compile(patternStr);

    public GoogleTranslator() {
        updatePattern();
    }

    /**
     * 更新Google翻译结果html的正则提取模式
     */
    private void updatePattern() {
        new Thread(() -> {
            String tempPatternStr = OkHttpUtil.get(patternUrl, null);
            if (tempPatternStr == null) return;
            Logger.getLogger(this.getClass().getName()).info("remote pattern:" + tempPatternStr);
            if (!patternStr.equals(tempPatternStr)) {
                Logger.getLogger(this.getClass().getName()).info("remote pattern有更新，正在更新Pattern");
                patternStr = tempPatternStr;
                translationPattern = Pattern.compile(patternStr);
            } else {
                Logger.getLogger(this.getClass().getName()).info("远端和本地端Google Pattern相同，无需更新");
            }
        }).start();
    }

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
     * 失败,null
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
        String pageUrl = "https://translate.google.cn/m";
        Map<String, String> param = new HashMap<>();
        param.put("sl", source);
        param.put("tl", dest);
        param.put("q", URLEncoder.encode(text, "UTF-8"));
        result = OkHttpUtil.get(pageUrl, param);
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
