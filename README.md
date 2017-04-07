# Retrofit Mock Result

Retrofit Mock Api Manager，Retrofit 模拟网络请求接口结果工具。

原理介绍：https://twiceyuan.com/2017/04/07/instantiating-an-abstract-class-in-java/

看了官方项目中的 https://github.com/square/retrofit/tree/master/retrofit-mock 模块，感觉写测试代码和配置都比较复杂，大多数时候我需要的是：

1. 对需要模拟响应数据的接口编写独立的测试数据（直接数据实体），而不是尽可能模拟真实的网络请求
2. 开启和关闭可以方便的配置

该项目实现了以下功能：

首先有一个供 Retrofit inflate 的 ApiInterface：

```java
public interface RealApi {

    @GET("api1")
    Observable<String> api1();
    
    @GET("api2")
    Observable<String> api2();
    
    @GET("api3")
    Observable<String> api3();
    
    //...其他接口
}
```

其中，api1，api2 是服务端已经实现的，我们只希望实现 api3 的数据模拟，并且保留对 RealApi 的引用。所以只需要编写以下代码：

```java
@MockApi
public abstract class MockApi implements RealApi {

    // 因为是抽象类，所以可以只实现需要模拟数据的接口方法，MockManager 会根据有没有定义模拟接口来判断是否使用模拟数据
    Observable<String> api3() {
        return Observable.just("mock data");
    }
}
```

之后获得带有 Mock 实现的 RealApi 对象：

```java
RealApi api = MockManager.build(ApiManager.getRealApi(), MockApi.class);

api.api1().subscribe(); // 真实接口
api.api2().subscribe(); // 真实接口
api.api3().subscribe(); // 模拟数据
```

## Gradle

```

repositories {
    maven { url 'https://jitpack.io' }
}

compile 'com.github.twiceyuan.RetrofitMockResult:processor:5f31d1f05f'
compile 'com.github.twiceyuan.RetrofitMockResult:core:5f31d1f05f'
```

## License

```
Copyright 2017 twiceYuan.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
