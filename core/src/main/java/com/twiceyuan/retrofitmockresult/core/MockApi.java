package com.twiceyuan.retrofitmockresult.core;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注解该类为一个模拟请求类。
 *
 * 需要该类为一个抽象类，之后实现
 */
@Target(ElementType.TYPE)
@Documented
@Retention(RetentionPolicy.SOURCE)
public @interface MockApi {

    /**
     * 是否开启，可以设置 BuildConfig 避免代入线上
     *
     * @return 是否开启该 Api
     */
    boolean enable() default true;
}
