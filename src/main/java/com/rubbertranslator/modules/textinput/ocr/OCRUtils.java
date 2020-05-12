package com.rubbertranslator.modules.textinput.ocr;

import com.rubbertranslator.utils.JsonUtil;
import com.rubbertranslator.utils.OkHttpUtil;
import okhttp3.FormBody;
import okhttp3.RequestBody;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Raven
 * @version 1.0
 * @date 2020/5/7 16:04
 */
public class OCRUtils {
    private static String API_KEY;
    private static String SECRET_KEY;
    private static String token = null;

    public static void setApiKey(String apiKey) {
        API_KEY = apiKey;
    }

    public static void setSecretKey(String secretKey) {
        SECRET_KEY = secretKey;
    }

    /**
     * ocr
     *
     * @param image ocr图片输入
     * @return 成功，返回ocr结果
     * 失败，返回null
     */
    public static String ocr(Image image) throws IOException {
        //
        if (image == null) return null;
        if(API_KEY == null || SECRET_KEY == null) return null;
        // 更新token
        token = updateToken();
        if(token == null) return null;
        // 请求
        final String ocrUrl = "https://aip.baidubce.com/rest/2.0/ocr/v1/accurate_basic";
        String result;
        RequestBody requestBody = new FormBody.Builder()
                .add("access_token", token)
                .add("image", convertToBase64Encode(image))
                .build();
        result = OkHttpUtil.syncPostRequest(ocrUrl, requestBody);
        if (result != null) {
            OCRResult ocrResult = JsonUtil.deserialize(result, OCRResult.class);
            if (ocrResult == null) result = null;
            else result = ocrResult.getCombinedWords();
        }
        Logger.getLogger(OCRUtils.class.getName()).log(Level.INFO, result);
        return result;
    }

    /**
     * 如果用户常年不重启进程，这里由bug，不过bug出现的机率比较小，更好的做法是比较expiredTime
     *
     * @return 当前可用token
     *         成功：有效token
     *         失败：null
     */
    private static String updateToken() throws IOException {
        String cache = token;
        // 如果用户常年不重启进程，这里有bug，不过bug出现的机率比较小，更好的做法是比较expiredTime
        if (cache == null) {
            String json = getToken();
            if (json == null) return null;

            OCRTokenEntity tokenEntity = JsonUtil.deserialize(json, OCRTokenEntity.class);
            if (tokenEntity == null) return null;

            cache = tokenEntity.getAccessToken();
            Logger.getLogger(OCRUtils.class.getName()).log(Level.INFO, "OCR Token:" + cache);
        }
        return cache;
    }

    private static String getToken() throws IOException {
        String result;
        final String tokenUrl = "https://aip.baidubce.com/oauth/2.0/token";
        RequestBody requestBody = new FormBody.Builder()
                .add("grant_type", "client_credentials")
                .add("client_id", API_KEY)
                .add("client_secret", SECRET_KEY)
                .build();
        result = OkHttpUtil.syncPostRequest(tokenUrl, requestBody);
        Logger.getLogger(OCRUtils.class.getName()).log(Level.INFO, result);
        return result;
    }


    private static String convertToBase64Encode(Image image) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        // 怎么判断图片是jpg还是png
        ImageIO.write((RenderedImage) image, "png", bos);
        byte[] imgData = bos.toByteArray();
        return Base64.getEncoder().encodeToString(imgData);
    }


}
