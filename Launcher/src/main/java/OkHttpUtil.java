import okhttp3.*;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Raven
 * @version 1.0
 * date 2020/5/7 16:11
 * okhttp工具类
 */
public class OkHttpUtil {

    public static OkHttpClient okHttpClient = OkHttpConfiguration.getInstance();

    /**
     * get
     *
     * @param url     请求的url
     * @param queries 请求的参数，在浏览器？后面的数据，没有可以传null
     * @return 成功 responseBody (String)
     *          失败 null
     */
    public static String get(String url, Map<String, String> queries) {
        String result = null;
        StringBuilder sb = new StringBuilder(url);
        if (queries != null && queries.keySet().size() > 0) {
            boolean firstFlag = true;
            for (Map.Entry<String,String> entry : queries.entrySet()) {
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
     *          失败 null
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

    private static class OkHttpConfiguration {

        private static OkHttpClient client = null;

        public static OkHttpClient getInstance() {
            if (client == null) {
                synchronized (OkHttpClient.class) {
                    if (client == null) {
                        client = new OkHttpClient.Builder()
                                .sslSocketFactory(Objects.requireNonNull(sslSocketFactory()), x509TrustManager())
                                .retryOnConnectionFailure(false)
                                .connectionPool(pool())
                                .connectTimeout(3, TimeUnit.SECONDS)
                                .readTimeout(3, TimeUnit.SECONDS)
                                .writeTimeout(3, TimeUnit.SECONDS)
                                .build();
                    }
                }
            }
            return client;
        }

        public static X509TrustManager x509TrustManager() {
            return new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                }

                @Override
                public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            };
        }

        public static SSLSocketFactory sslSocketFactory() {
            try {
                //信任任何链接
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, new TrustManager[]{x509TrustManager()}, new SecureRandom());
                return sslContext.getSocketFactory();
            } catch (NoSuchAlgorithmException | KeyManagementException e) {
                Logger.getLogger(OkHttpUtil.class.getName(), "初始化SSL失败");
            }
            return null;
        }

        /**
         * Create a new connection pool with tuning parameters appropriate for a single-user application.
         * The tuning parameters in this pool are subject to change in future OkHttp releases. Currently
         */
        public static ConnectionPool pool() {
            return new ConnectionPool(200, 5, TimeUnit.MINUTES);
        }
    }

}
