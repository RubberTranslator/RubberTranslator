package com.rubbertranslator.modules.translate.baidu;

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
 * date 2020/5/8 14:37
 */
public class BaiduTranslator extends AbstractTranslator {

    @Override
    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    @Override
    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    // https://gss0.bdstatic.com/70cFfyinKgQIm2_p8IuM_a/daf/pic/item/91ef76c6a7efce1b1fde01aca051f3deb58f65db.jpg
    @Override
    public void addLanguageMap() {
        langMap.put(Language.AUTO, "auto");
        langMap.put(Language.CHINESE_SIMPLIFIED, "zh");
        langMap.put(Language.CHINESE_TRADITIONAL, "cht");
        langMap.put(Language.ENGLISH, "en");
        langMap.put(Language.FRENCH, "fra");
        langMap.put(Language.JAPANESE, "jp");
    }

    /**
     * baidu翻译
     *
     * @param source 源语言
     * @param dest   目标语言
     * @param text   需要翻译的文本
     * @return null，如果翻译不成功
     * 翻译后的文本
     */
    @Override
    public String translate(Language source, Language dest, String text) {
        // 百度中文特殊处理
        String translatedText = null;
        try {
            BaiduTranslationResult baiduTranslateResult = doTranslate(
                    langMap.get(source), langMap.get(dest), text);
            if (baiduTranslateResult != null) {
                translatedText = mergeTranslatedText(baiduTranslateResult);
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, translatedText);
            }
        } catch (IOException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "百度翻译失败", e);
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getLocalizedMessage(), e);
        }
        return translatedText;
    }

    private BaiduTranslationResult doTranslate(String source, String dest, String text) throws IOException {
        if(appKey == null || secretKey == null) return null;
        String URL = "https://fanyi-api.baidu.com/api/trans/vip/translate";
        // 随机数
        String salt = String.valueOf(System.currentTimeMillis());
        // 加密前的原文
        String src = appKey + text + salt + secretKey;
        String sign = DigestUtil.md5(src);

        RequestBody requestBody = new FormBody.Builder()
                .add("q", text)
                .add("from", source)
                .add("to", dest)
                .add("appid", appKey)
                .add("salt", salt)
                .add("sign", sign)
                .build();
        String json = OkHttpUtil.syncPostRequest(URL, requestBody);
        BaiduTranslationResult deserialize = JsonUtil.deserialize(json, BaiduTranslationResult.class);
        if (deserialize != null && deserialize.getErrorCode() == null) {
            return deserialize;
        } else {
            return null;
        }
    }

    /**
     * 合并翻译后的文本
     *
     * @param result 百度翻译结果对应
     * @return 合并后的文本
     */
    private String mergeTranslatedText(BaiduTranslationResult result) {
        StringBuilder sb = new StringBuilder();
        for (BaiduTranslationResult.TransResultItem item : result.getTransResult()) {
            sb.append(item.getDst()).append("\n");
        }
        // 删除最后的"\n"
        sb.delete(sb.length()-1,sb.length());
        return sb.toString();
    }

}
