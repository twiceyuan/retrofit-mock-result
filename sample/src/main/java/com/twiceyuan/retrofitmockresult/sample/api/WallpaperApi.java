package com.twiceyuan.retrofitmockresult.sample.api;

import com.google.gson.JsonElement;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by twiceYuan on 2017/4/5.
 * <p>
 * 壁纸 API
 * <p>
 * https://github.com/xCss/bing
 */
@SuppressWarnings("WeakerAccess")
public interface WallpaperApi {

    /**
     * 查询壁纸
     *
     * @param day      自今日起第d天前的数据
     * @param width    图片宽度
     * @param height   图片高度
     * @param page     第x页
     * @param size     每页条数
     * @param callback JSONP 的回调函数名
     * @return 壁纸数据
     */
    @GET("v1")
    Observable<JsonElement> query(@Query("d") Integer day,
                                  @Query("w") Integer width,
                                  @Query("h") Integer height,
                                  @Query("p") Integer page,
                                  @Query("size") Integer size,
                                  @Query("callback") String callback);

    @GET("mock1")
    Observable<String> testMock1();

    @GET("mock2")
    Observable<String> testMock2();

    @GET("mock3")
    Observable<String> testMock3();

    @GET("mock4")
    Observable<String> testMock4();

    @GET("mock5")
    Observable<String> testMock5();

    @GET("mock6")
    Observable<String> testMock6();
}
