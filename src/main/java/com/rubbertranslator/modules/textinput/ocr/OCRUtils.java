package com.rubbertranslator.modules.textinput.ocr;

import com.rubbertranslator.test.Configuration;
import com.rubbertranslator.utils.JsonUtils;
import com.rubbertranslator.utils.OkHttpUtils;
import okhttp3.*;

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
    //TODO: 改用配置文件
    private static final String API_KEY = Configuration.API_KEY;
    private static final String SECRET_KEY = Configuration.SECRET_KEY;
    private static String token = null;

    /**
     * ocr
     * @param image ocr图片输入
     * @return 成功，返回ocr结果
     *         失败，返回null
     */
    public static String ocr(Image image) throws IOException {
        if (image == null) return null;
        token = updateToken();

        final String ocrUrl = "https://aip.baidubce.com/rest/2.0/ocr/v1/accurate_basic";
        String result;
        RequestBody requestBody = new FormBody.Builder()
                .add("access_token",token)
                .add("image",convertToBase64Encode(image))
                .build();
        result = OkHttpUtils.syncPostRequest(ocrUrl,requestBody);
        if(result != null){
            OCRResult ocrResult = JsonUtils.deserialize(result, OCRResult.class);
            if(ocrResult == null) result =null;
            else result = ocrResult.getCombinedWords();
        }
        Logger.getLogger(OCRUtils.class.getName()).log(Level.INFO,result);
        return result;
    }

    /**
     * 如果用户常年不重启进程，这里由bug，不过bug出现的机率比较小，更好的做法是比较expiredTime
     * @return 当前可用token
     */
    private static String updateToken() throws IOException {
        String cache = token;
        // 如果用户常年不重启进程，这里由bug，不过bug出现的机率比较小，更好的做法是比较expiredTime
        if(cache == null){
            String json = getToken();
            if(json == null) return null;

            OCRTokenEntity tokenEntity = JsonUtils.deserialize(json, OCRTokenEntity.class);
            if(tokenEntity == null) return null;

            cache = tokenEntity.getAccessToken();
            Logger.getLogger(OCRUtils.class.getName()).log(Level.INFO,"OCR Token:"+cache);
        }
        return cache;
    }

    private static String getToken() throws IOException {
        String result;
        final String tokenUrl = "https://aip.baidubce.com/oauth/2.0/token";
        RequestBody requestBody = new FormBody.Builder()
                .add("grant_type","client_credentials")
                .add("client_id",API_KEY)
                .add("client_secret",SECRET_KEY)
                .build();
        result = OkHttpUtils.syncPostRequest(tokenUrl,requestBody);
        Logger.getLogger(OCRUtils.class.getName()).log(Level.INFO,result);
        return result;
    }



    private static String convertToBase64Encode(Image image) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        // 怎么判断图片是jpg还是png
        ImageIO.write((RenderedImage) image,"png",bos);
        byte[] imgData = bos.toByteArray();
        return Base64.getEncoder().encodeToString(imgData);
    }


}
