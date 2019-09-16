package com.microbotic.temperature.app;

import android.annotation.SuppressLint;
import android.content.Context;

import com.microbotic.temperature.BuildConfig;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;

import static com.microbotic.temperature.app.Config.BASE_URL;



public class ApiClient {
    private static Retrofit retrofit = null;

    public static Retrofit getClient(Context context) {


        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            int cacheSize = 10 * 1024 * 1024; // 10 MB
            Cache cache = new Cache(context.getCacheDir(), cacheSize);

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

            //want to avoid custom url encoding , here is solution
            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request request = chain.request();
                    String string = request.url().toString();
                    string = string.replace("%2C", ",");
                    //string = string.replace("%3D", "=");
                    Request newRequest = new Request.Builder()
                            .url(string)
                            .build();
                    return chain.proceed(newRequest);
                }
            });

            httpClient.cache(cache);
            httpClient.connectTimeout(1, TimeUnit.MINUTES);
            httpClient.readTimeout(1, TimeUnit.MINUTES);
            httpClient.writeTimeout(1, TimeUnit.MINUTES);
            httpClient.followRedirects(true);
            httpClient.followSslRedirects(true);


            if (BuildConfig.DEBUG) {
                httpClient.addInterceptor(logging);
            }

            Retrofit.Builder builder = new Retrofit.Builder().baseUrl(BASE_URL);
            retrofit = builder.client(httpClient.build())
                    //.client(getUnsafeOkHttpClient().build())
                    .build();
        }

        return retrofit;

    }

    public static Retrofit getClient() {
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);


            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

            //want to avoid custom url encoding , here is solution
            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request request = chain.request();
                    String string = request.url().toString();
                    string = string.replace("%2C", ",");
                    //string = string.replace("%3D", "=");
                    Request newRequest = new Request.Builder()
                            .url(string)
                            .build();
                    return chain.proceed(newRequest);
                }
            });

            httpClient.connectTimeout(1, TimeUnit.MINUTES);
            httpClient.readTimeout(1, TimeUnit.MINUTES);
            httpClient.writeTimeout(1, TimeUnit.MINUTES);
            httpClient.followRedirects(true);
            httpClient.followSslRedirects(true);


            if (BuildConfig.DEBUG) {
                httpClient.addInterceptor(logging);
            }

            Retrofit.Builder builder = new Retrofit.Builder().baseUrl(BASE_URL);
            retrofit = builder.client(httpClient.build())
                    //.client(getUnsafeOkHttpClient().build())
                    .build();
        }

        return retrofit;

    }

    private static OkHttpClient.Builder getUnsafeOkHttpClient() {

        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @SuppressLint("TrustAllX509TrustManager")
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            return builder;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
