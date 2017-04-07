package com.twiceyuan.retrofitmockresult.sample.api;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by twiceYuan on 2017/4/5.
 *
 * ApiManager
 */
public class ApiManager {

    private static WallpaperApi sApi;

    public static WallpaperApi getApi() {
        if (sApi == null) {

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .build();

            sApi = new Retrofit.Builder()
                    .baseUrl("https://bing.ioliu.cn")
                    .client(client)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(WallpaperApi.class);


        }
        return sApi;
    }
}
