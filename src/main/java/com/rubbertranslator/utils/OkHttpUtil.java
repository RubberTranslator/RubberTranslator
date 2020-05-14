package com.rubbertranslator.utils;

import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author Raven
 * @version 1.0
 * date 2020/5/7 16:11
 * okhttp工具类
 */
public class OkHttpUtil {

    private static OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(3, TimeUnit.SECONDS)
            .readTimeout(3,TimeUnit.SECONDS)
            .build();

    public static OkHttpClient getInstance() {
        return client;
    }

    /**
     * 同步请求
     *
     * @param url
     * @param requestBody
     * @return 如果请求成功（状态码在[200-300)之间)，则返回相应结果字符串
     * 如果请求失败，返回null
     * @throws IOException
     */
    public static String syncPostRequest(String url, RequestBody requestBody) throws IOException {
        Request request = new Request.Builder()
                .url(url).post(requestBody).build();
        // 同步
        return syncRequest(request);
    }

    public static String syncRequest(Request request) throws IOException {
        String result = null;
        Response response = client.newCall(request).execute();
        ResponseBody responseBody = response.body();
        if (response.isSuccessful()) {
            result = responseBody.string(); // execute返回的response.body不会返回null
        }
        responseBody.close();
        return result;
    }
}
