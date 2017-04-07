package com.twiceyuan.retrofitmockresult.sample.api;

import com.twiceyuan.retrofitmockresult.core.MockApi;

import io.reactivex.Observable;

/**
 * Created by twiceYuan on 2017/4/6.
 *
 * 模拟请求结果
 */
@MockApi(enable = true)
public abstract class MockWallpaperApi implements WallpaperApi {

    @Override
    public Observable<String> testMock1() {
        return Observable.just("Hello");
    }

    @Override
    public Observable<String> testMock2() {
        return Observable.just("Hello2");
    }
}
