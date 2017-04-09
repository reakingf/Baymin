package com.qa.fgj.baymin.net;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * 用于获取与设置cookie，上下文情景分析用
 * Created by FangGengjia on 2017/4/9.
 */

public class CookieInterceptor implements Interceptor {

    private static String COOKIE = null;

    @Override
    public Response intercept(Chain chain) throws IOException {

        Response response;

        if (COOKIE != null) {
            //如果本地存在上次保存cookie，头部添加cookie
            Request request = chain.request();
            Request.Builder builder = request.newBuilder();
            Request customRequest = builder.addHeader("Cookie", COOKIE).build();
            response = chain.proceed(customRequest);
        } else {
            response = chain.proceed(chain.request());
        }

        //存储最新的cookie
        if (!response.headers("Set-Cookie").isEmpty()) {
            final StringBuffer cookieBuffer = new StringBuffer();
            Observable.from(response.headers("Set-Cookie"))
                    .map(new Func1<String, String>() {
                        @Override
                        public String call(String s) {
                            String[] cookieArray = s.split(";");
                            return cookieArray[0];
                        }
                    })
                    .subscribe(new Action1<String>() {
                        @Override
                        public void call(String cookie) {
                            cookieBuffer.append(cookie).append(";");
                        }
                    });
            COOKIE = cookieBuffer.toString();
        }

        return response;
    }

}
