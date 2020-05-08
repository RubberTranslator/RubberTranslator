package com.rubbertranslator.modules.translate.baidu;

import com.rubbertranslator.modules.translate.TranslatorEngine;
import com.rubbertranslator.test.Configuration;
import com.rubbertranslator.utils.JsonUtil;
import com.rubbertranslator.utils.MD5Util;
import com.rubbertranslator.utils.OkHttpUtil;
import okhttp3.FormBody;
import okhttp3.RequestBody;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * @author Raven
 * @version 1.0
 * @date 2020/5/8 14:37
 */
public class BaiduTranslator implements TranslatorEngine {
    private final String URL = "https://fanyi-api.baidu.com/api/trans/vip/translate";
    private final String APP_ID = Configuration.BAIDU_TRANSLATE_API_KEY;
    private final String SECRETE_KEY = Configuration.BAIDU_TRANSLATE_SECRET_KEY;

    /**
     * baidu翻译
     * @param source 源语言
     * @param dest 目标语言
     * @param text 需要翻译的文本
     * @return null，如果翻译不成功
     *         翻译后的文本
     */
    @Override
    public String translate(String source, String dest, String text) {
        String translatedText = null;
        try {
            BaiduTranslateResult baiduTranslateResult = doTranslate(source,dest,text);
            if(baiduTranslateResult != null){
                translatedText = formatTranslatedText(baiduTranslateResult);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return translatedText;
    }

    private BaiduTranslateResult doTranslate(String source, String dest, String text) throws IOException {
        // 随机数
        String salt = String.valueOf(System.currentTimeMillis());
        // 加密前的原文
        String src = APP_ID + text + salt + SECRETE_KEY;
        String sign = MD5Util.md5(src);

        RequestBody requestBody = new FormBody.Builder()
                .add("q", text)
                .add("from",source)
                .add("to",dest)
                .add("appid",APP_ID)
                .add("salt",salt)
                .add("sign",sign)
                .build();
        String json = OkHttpUtil.syncPostRequest(URL,requestBody);
        Logger.getLogger(this.getClass().getName()).info(json);
        BaiduTranslateResult deserialize = JsonUtil.deserialize(json, BaiduTranslateResult.class);
        if(deserialize.getErrorCode() == null){
            return deserialize;
        }else{
            return null;
        }
    }

    /**
     * 格式化翻译后的文本对象
     * @param result 最终的翻译文本
     * @return
     */
    private String formatTranslatedText(BaiduTranslateResult result){
        StringBuilder sb = new StringBuilder();
        for(BaiduTranslateResult.TransResultItem item : result.getTransResult()){
            sb.append(item.getDst()).append("\n");
        }
        return sb.toString();
    }
}
