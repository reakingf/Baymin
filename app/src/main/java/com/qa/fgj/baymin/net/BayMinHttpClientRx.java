package com.qa.fgj.baymin.net;

import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 使用Retrofit2 + okhttp3实现的通用网络请求库
 * 结果返回使用RxJava风格
 * Created by FangGengjia on 2017/1/24.
 */

public class BayMinHttpClientRx {

    private final Retrofit retrofit;
    private final static String BASE_URL = "http://www.baidu.com";

    public BayMinHttpClientRx(final long connTimeout, final long readTimeout, final long writeTimeout){

        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        if(connTimeout > 0) {
            httpClientBuilder.connectTimeout(connTimeout, TimeUnit.MILLISECONDS);
        }
        if(readTimeout > 0){
            httpClientBuilder.readTimeout(readTimeout, TimeUnit.MILLISECONDS);
        }
        if(writeTimeout > 0){
            httpClientBuilder.writeTimeout(writeTimeout, TimeUnit.MILLISECONDS);
        }

        Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create());

        retrofit = retrofitBuilder.client(httpClientBuilder.build()).build();
    }

    public BayMinHttpClientRx() {
        this(-1, -1, -1);
    }

    public <S> S createApi(Class<S> apiClass){
        return retrofit.create(apiClass);
    }
}
