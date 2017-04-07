package com.twiceyuan.retrofitmockresult.core;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.logging.Logger;

/**
 * Created by twiceYuan on 2017/4/6.
 * <p>
 * Mock Api 管理
 */
public class MockManager {

    public static <Origin, Mock extends Origin> Origin build(final Origin origin, final Class<Mock> mockClass) {

        // 如果 Mock Class 标记为关闭，则直接返回真实接口对象
        if (!isEnable(mockClass)) {
            return origin;
        }

        // 因为 Android 平台不支持 Javassist 依赖的动态 Byte Code 支持，
        // 所以使用 Annotation Processor 动态生成的代码反射调用来实现相同的功能。
        final Mock mockObject = getImplObject(mockClass);

        Class<?> originClass = origin.getClass().getInterfaces()[0];

        //noinspection unchecked
        return (Origin) Proxy.newProxyInstance(originClass.getClassLoader(), new Class[]{originClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                // 获取定义的抽象类中的同名方法，判断是否已经实现
                Method mockMethod = null;
                try {
                    mockMethod = mockClass.getDeclaredMethod(method.getName(), method.getParameterTypes());
                } catch (NoSuchMethodException e) {
                    Logger.getAnonymousLogger().info(e.getMessage());
                }
                if (mockMethod == null || Modifier.isAbstract(mockMethod.getModifiers())) {
                    return method.invoke(origin, objects);
                } else {
                    return mockMethod.invoke(mockObject, objects);
                }
            }
        });
    }

    private static boolean isEnable(Class cls) {
        try {
            Class mockImplClass = Class.forName(cls.getCanonicalName() + Constants.SUFFIX);
            return (boolean) mockImplClass.getDeclaredField(Constants.ENABLE).get(null);
        } catch (NoSuchFieldException e) {
            return false;
        } catch (IllegalAccessException e) {
            return false;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    // 获得生成代码构造的对象
    private static <T> T getImplObject(Class<T> cls) {
        try {
            return (T) Class.forName(cls.getName() + Constants.SUFFIX).newInstance();
        } catch (Exception e) {
            return null;
        }
    }
}
