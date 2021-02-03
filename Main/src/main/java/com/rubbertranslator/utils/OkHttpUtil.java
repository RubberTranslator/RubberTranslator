package com.rubbertranslator.utils;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Raven
 * @version 1.0
 * date 2020/5/7 16:11
 * okhttp工具类
 */
public class OkHttpUtil {

    private static OkHttpClient okHttpClient = OkHttpConfiguration.getInstance();

    /**
     * get
     *
     * @param url     请求的url
     * @param queries 请求的参数，在浏览器？后面的数据，没有可以传null
     * @return 成功 responseBody (String)
     * 失败 null
     */
    public static String get(String url, Map<String, String> queries) {
        String result = null;
        StringBuilder sb = new StringBuilder(url);
        if (queries != null && queries.keySet().size() > 0) {
            boolean firstFlag = true;
            for (Map.Entry<String, String> entry : queries.entrySet()) {
                if (firstFlag) {
                    sb.append("?").append(entry.getKey()).append("=").append(entry.getValue());
                    firstFlag = false;
                } else {
                    sb.append("&").append(entry.getKey()).append("=").append(entry.getValue());
                }
            }
        }
        Request request = new Request.Builder()
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36")
                .addHeader("Connection", "close")
                .url(sb.toString())
                .build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                result = Objects.requireNonNull(response.body()).string();
            }
        } catch (Exception e) {
            Logger.getLogger(OkHttpClient.class.getName()).log(Level.SEVERE, "get请求失败");
            Logger.getLogger(OkHttpClient.class.getName()).log(Level.SEVERE, e.getLocalizedMessage());
        }
        return result;
    }

    /**
     * post
     *
     * @param url    请求的url
     * @param params post form 提交的参数
     * @return 成功 responseBody (String)
     * 失败 null
     */
    public static String post(String url, Map<String, String> params) {
        String result = null;
        FormBody.Builder builder = new FormBody.Builder();
        //添加参数
        if (params != null && params.keySet().size() > 0) {
            for (String key : params.keySet()) {
                builder.add(key, params.get(key));
            }
        }
        Request request = new Request.Builder()
                .url(url)
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36")
                .addHeader("Connection", "close")
                .post(builder.build())
                .build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                result = Objects.requireNonNull(response.body()).string();
            }
        } catch (Exception e) {
            Logger.getLogger(OkHttpClient.class.getName()).log(Level.SEVERE, "post请求失败");
            Logger.getLogger(OkHttpClient.class.getName()).log(Level.SEVERE, e.getLocalizedMessage());
        }
        return result;
    }

}
