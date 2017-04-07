package com.twiceyuan.retrofitmockresult.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.gson.JsonElement;
import com.twiceyuan.retrofitmockresult.core.MockManager;
import com.twiceyuan.retrofitmockresult.sample.api.ApiManager;
import com.twiceyuan.retrofitmockresult.sample.api.MockWallpaperApi;
import com.twiceyuan.retrofitmockresult.sample.api.WallpaperApi;

import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


@SuppressWarnings("DanglingJavadoc")
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WallpaperApi api = MockManager.build(ApiManager.getApi(), MockWallpaperApi.class);

        /**
         * 调用真实 Api 方法
         */
        api.query(null, null, null, 1, 1, null).subscribeOn(Schedulers.newThread()).subscribe(new Consumer<JsonElement>() {
            @Override
            public void accept(JsonElement element) throws Exception {
                Log.i(TAG, element.toString());
            }
        });

        /**
         * 调用 {@link MockWallpaperApi} 中定义的模拟请求
         */
        api.testMock1().subscribeOn(Schedulers.newThread()).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.i(TAG, s);
            }
        });
    }
}
