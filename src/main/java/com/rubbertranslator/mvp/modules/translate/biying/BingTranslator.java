package com.rubbertranslator.mvp.modules.translate.biying;

import com.rubbertranslator.mvp.modules.translate.AbstractTranslator;
import com.rubbertranslator.enumtype.Language;
import com.rubbertranslator.utils.OkHttpUtil;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Raven
 * @version 1.0
 * date  2020/5/18 15:13
 */
public class BingTranslator extends AbstractTranslator {
    @Override
    public void addLanguageMap() {
        langMap.put(Language.AUTO,"auto-detect");
        langMap.put(Language.CHINESE_SIMPLIFIED,"zh-Hans");
        langMap.put(Language.CHINESE_TRADITIONAL,"zh-Hant");
        langMap.put(Language.ENGLISH,"en");
        langMap.put(Language.FRENCH,"fr");
        langMap.put(Language.JAPANESE,"ja");
    }

    /**
     *
     * @param source 源语言
     * @param dest   目标语言
     * @param text   需要翻译的文本
     * @return
     */
    @Override
    public String translate(Language source, Language dest, String text) {
        String translatedText = null;
        try {
            translatedText = doTranslate(
                    langMap.get(source), langMap.get(dest), text);
            Logger.getLogger(this.getClass().getName()).info(translatedText);
        } catch (IOException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "必应翻译失败", e);
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getLocalizedMessage(), e);
        }
        return translatedText;
    }

    private String doTranslate(String source, String dest, String text) throws IOException {
        // 暂时无法支持
        String url = "";
        RequestBody body = new FormBody.Builder()
                .add("fromLang",source)
                .add("to",dest)
                .add("text",text)
                .build();
        Request request = new Request.Builder()
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36")
                .url(url)
                .post(body)
                .build();
        return OkHttpUtil.syncRequest(request);
    }
}
