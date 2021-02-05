package com.octopus.mapmarks.util;

import android.util.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * 参考：
 * https://blog.csdn.net/u013651026/article/details/81058524?utm_medium=distribute.pc_relevant_download.none-task-blog-baidujs-1.nonecase&depth_1-utm_source=distribute.pc_relevant_download.none-task-blog-baidujs-1.nonecase
 */
public class RetrofitServiceManager {

    private static final int DEFAULT_TIME_OUT = 5;//超时时间 5s
    private static final int DEFAULT_READ_TIME_OUT = 10;
    private Retrofit mRetrofit;

    private RetrofitServiceManager() {

        // 创建 OKHttpClient
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS);//连接超时时间
        builder.writeTimeout(DEFAULT_READ_TIME_OUT, TimeUnit.SECONDS);//写操作 超时时间
        builder.readTimeout(DEFAULT_READ_TIME_OUT, TimeUnit.SECONDS);//读操作超时时间

        // 添加公共参数拦截器
        HttpCommonInterceptor commonInterceptor = new HttpCommonInterceptor.Builder().addHeaderParams("paltform", "android").addHeaderParams("userToken", "1234343434dfdfd3434").addHeaderParams("userId", "123445").build();
        builder.addInterceptor(commonInterceptor);

        // 创建Retrofit
        mRetrofit = new Retrofit.Builder()
                .client(builder.build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(ApiConfig.BASE_URL)
                .build();
    }

    private static class SingletonHolder {
        private static final RetrofitServiceManager INSTANCE = new RetrofitServiceManager();
    }

    /**
     * 获取RetrofitServiceManager
     *
     * @return
     */
    public static RetrofitServiceManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 获取对应的Service
     *
     * @param service Service 的 class
     * @param <T>
     * @return
     */
    public <T> T create(Class<T> service) {
        return mRetrofit.create(service);
    }

    /*
     * 拦截器
     *
     * 向请求头里添加公共参数
     */
    public static class HttpCommonInterceptor implements Interceptor {
        private Map<String, String> mHeaderParamsMap = new HashMap<>();

        public HttpCommonInterceptor() {
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Log.d("HttpCommonInterceptor", "add common params");
            Request oldRequest = chain.request();
            // 添加新的参数，添加到url 中
                 /*HttpUrl.Builder authorizedUrlBuilder = oldRequest.url().newBuilder()
                     .scheme(oldRequest.url().scheme())
                      .host(oldRequest.url().host());*/

            // 新的请求
            Request.Builder requestBuilder = oldRequest.newBuilder();
            requestBuilder.method(oldRequest.method(), oldRequest.body());

            //添加公共参数,添加到header中
            if (mHeaderParamsMap.size() > 0) {
                for (Map.Entry<String, String> params : mHeaderParamsMap.entrySet()) {
                    requestBuilder.header(params.getKey(), params.getValue());
                }
            }
            Request newRequest = requestBuilder.build();
            return chain.proceed(newRequest);
        }

        public static class Builder {
            HttpCommonInterceptor mHttpCommonInterceptor;

            public Builder() {
                mHttpCommonInterceptor = new HttpCommonInterceptor();
            }

            public Builder addHeaderParams(String key, String value) {
                mHttpCommonInterceptor.mHeaderParamsMap.put(key, value);
                return this;
            }

            public Builder addHeaderParams(String key, int value) {
                return addHeaderParams(key, String.valueOf(value));
            }

            public Builder addHeaderParams(String key, float value) {
                return addHeaderParams(key, String.valueOf(value));
            }

            public Builder addHeaderParams(String key, long value) {
                return addHeaderParams(key, String.valueOf(value));
            }

            public Builder addHeaderParams(String key, double value) {
                return addHeaderParams(key, String.valueOf(value));
            }

            public HttpCommonInterceptor build() {
                return mHttpCommonInterceptor;
            }

        }
    }

}
