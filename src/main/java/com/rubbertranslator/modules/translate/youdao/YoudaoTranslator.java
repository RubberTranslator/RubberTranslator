package com.rubbertranslator.modules.translate.youdao;

import com.rubbertranslator.modules.translate.ITranslator;
import com.rubbertranslator.test.Configuration;
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
 * @date 2020/5/8 19:43
 * TODO: 考虑一下公共有道翻译接口（爬虫或者旧式翻译接口）
 * 旧式接口：http://fanyi.youdao.com/openapi.do?keyfrom=xinlei&key=759115437&type=data&doctype=json&version=1.1&q=Byte-addressable
 */
public class YoudaoTranslator implements ITranslator {

    @Override
    public String translate(String source, String dest, String text) {
        String translatedText = null;
        try {
            YoudaoTranslationResult translationResult = doTranslate(source, dest, text);
            if(translationResult != null){
                translatedText = mergeTranslatedText(translationResult);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
        }
        Logger.getLogger(this.getClass().getName()).info(translatedText);
        return translatedText;
    }

    private YoudaoTranslationResult doTranslate(String source, String dest, String text) throws IOException {
        String curtime = String.valueOf(System.currentTimeMillis() / 1000);
        String salt = String.valueOf(System.currentTimeMillis());
        String APP_KEY = Configuration.YOUDAO_TRANSLATE_API_KEY;
        String SECRET_KEY = Configuration.YOUDAO_TRANSLATE_SECRET_KEY;
        String signStr = APP_KEY + truncate(text) + salt + curtime + SECRET_KEY;
        String sign = DigestUtil.sha256(signStr);

        RequestBody requestBody = new FormBody.Builder()
                .add("from", source)
                .add("to",dest)
                .add("signType","v3")
                .add("curtime",curtime)
                .add("appKey", APP_KEY)
                .add("q", text)
                .add("salt", salt)
                .add("sign",sign)
                .build();

        String URL = "https://openapi.youdao.com/api";
        String json = OkHttpUtil.syncPostRequest(URL,requestBody);
        Logger.getLogger(this.getClass().getName()).info(json);
        YoudaoTranslationResult deserialize = JsonUtil.deserialize(json, YoudaoTranslationResult.class);
        if("0".equals(deserialize.getErrorCode())){
            return deserialize;
        }else{
            return null;
        }
    }

    /**
     * 合并翻译后的文本
     * @param result 有道翻译结果对应
     * @return  合并后的文本
     */
    private String mergeTranslatedText(YoudaoTranslationResult result){
        StringBuilder sb = new StringBuilder();
        for(String item : result.getTranslation()){
            sb.append(item).append("\n");
        }
        return sb.toString();
    }

    private  String truncate(String q) {
        if (q == null) {
            return null;
        }
        int len = q.length();
        return len <= 20 ? q : (q.substring(0, 10) + len + q.substring(len - 10, len));
    }
}
