package com.softdesign.devintensive.data.network;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.softdesign.devintensive.data.network.interceptors.HeaderInterceptor;
import com.softdesign.devintensive.utils.AppConfig;
import com.softdesign.devintensive.utils.DevIntensiveApplication;

import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by roman on 11.07.16.
 */
public class ServiceGenerator {

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    private static Retrofit.Builder sBuilder = new Retrofit.Builder()
            .baseUrl(AppConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create());

    public static <T> T createService(Class<T> serviceClass) {

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        httpClient.addInterceptor(new HeaderInterceptor());
        httpClient.addInterceptor(loggingInterceptor);
        httpClient.connectTimeout(AppConfig.MAX_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS);
        httpClient.readTimeout(AppConfig.MAX_READ_TIMEOUT, TimeUnit.MILLISECONDS);
        httpClient.cache(new Cache(DevIntensiveApplication.getAppContext().getCacheDir(), Integer.MAX_VALUE));
        httpClient.addNetworkInterceptor(new StethoInterceptor());

        Retrofit retrofit = sBuilder
                .client(httpClient.build())
                .build();
        return retrofit.create(serviceClass);
    }
}
