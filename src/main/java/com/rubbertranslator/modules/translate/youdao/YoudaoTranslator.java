package com.rubbertranslator.modules.translate.youdao;

import com.rubbertranslator.modules.translate.AbstractTranslator;
import com.rubbertranslator.modules.translate.Language;
import com.rubbertranslator.utils.DigestUtil;
import com.rubbertranslator.utils.JsonUtil;
import com.rubbertranslator.utils.OkHttpUtil;
import okhttp3.FormBody;
import okhttp3.RequestBody;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Raven
 * @version 1.0
 * date 2020/5/8 19:43
 * hack: 考虑一下公共有道翻译接口（爬虫或者旧式翻译接口）
 * 旧式接口：http://fanyi.youdao.com/openapi.do?keyfrom=xinlei&key=759115437&type=data&doctype=json&version=1.1&q=Byte-addressable
 */
public class YoudaoTranslator extends AbstractTranslator {

    private String APP_KEY;
    private String SECRET_KEY;

    public void setAPP_KEY(String APP_KEY) {
        this.APP_KEY = APP_KEY;
    }

    public void setSECRET_KEY(String SECRET_KEY) {
        this.SECRET_KEY = SECRET_KEY;
    }

    //http://ai.youdao.com/DOCSIRMA/html/%E8%87%AA%E7%84%B6%E8%AF%AD%E8%A8%80%E7%BF%BB%E8%AF%91/API%E6%96%87%E6%A1%A3/%E6%96%87%E6%9C%AC%E7%BF%BB%E8%AF%91%E6%9C%8D%E5%8A%A1/%E6%96%87%E6%9C%AC%E7%BF%BB%E8%AF%91%E6%9C%8D%E5%8A%A1-API%E6%96%87%E6%A1%A3.html
    @Override
    public void addLanguageMap() {
        langMap.put(Language.AUTO, "auto");
        langMap.put(Language.CHINESE_SIMPLIFIED, "zh-CHS");
        langMap.put(Language.CHINESE_TRADITIONAL, "zh-CHT");
        langMap.put(Language.ENGLISH, "en");
        langMap.put(Language.FRENCH, "fr");
        langMap.put(Language.JAPANESE, "ja");
    }

    @Override
    public String translate(Language source, Language dest, String text) {
        String translatedText = null;
        try {
            YoudaoTranslationResult translationResult = doTranslate(
                    langMap.get(source), langMap.get(dest), text);
            if (translationResult != null) {
                translatedText = mergeTranslatedText(translationResult);
            }
        } catch (IOException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "有道翻译失败", e);
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getLocalizedMessage(), e);
        }
        Logger.getLogger(this.getClass().getName()).info(translatedText);
        return translatedText;
    }

    private YoudaoTranslationResult doTranslate(String source, String dest, String text) throws IOException {
        if(APP_KEY == null || SECRET_KEY == null) return null;
        String curtime = String.valueOf(System.currentTimeMillis() / 1000);
        String salt = String.valueOf(System.currentTimeMillis());
        String signStr = APP_KEY + truncate(text) + salt + curtime + SECRET_KEY;
        String sign = DigestUtil.sha256(signStr);

        RequestBody requestBody = new FormBody.Builder()
                .add("from", source)
                .add("to", dest)
                .add("signType", "v3")
                .add("curtime", curtime)
                .add("appKey", APP_KEY)
                .add("q", text)
                .add("salt", salt)
                .add("sign", sign)
                .build();

        String URL = "https://openapi.youdao.com/api";
        String json = OkHttpUtil.syncPostRequest(URL, requestBody);
        Logger.getLogger(this.getClass().getName()).info(json);
        YoudaoTranslationResult deserialize = JsonUtil.deserialize(json, YoudaoTranslationResult.class);
        if ("0".equals(deserialize.getErrorCode())) {
            return deserialize;
        } else {
            return null;
        }
    }

    /**
     * 合并翻译后的文本
     *
     * @param result 有道翻译结果对应
     * @return 合并后的文本
     */
    private String mergeTranslatedText(YoudaoTranslationResult result) {
        StringBuilder sb = new StringBuilder();
        for (String item : result.getTranslation()) {
            sb.append(item).append("\n");
        }
        sb.delete(sb.length()-1,sb.length());
        return sb.toString();
    }

    private String truncate(String q) {
        if (q == null) {
            return null;
        }
        int len = q.length();
        return len <= 20 ? q : (q.substring(0, 10) + len + q.substring(len - 10, len));
    }
}
