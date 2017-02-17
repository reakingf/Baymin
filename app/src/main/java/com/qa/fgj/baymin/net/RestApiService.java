package com.qa.fgj.baymin.net;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;


/**
 * 全局API单例构造器
 * Created by FangGengjia on 2017/02/14.
 */
public final class RestApiService {
    private static RestApiService instance;
    private final BayMinHttpClientRx bayMinHttpClientRx;

    public static RestApiService createInstance(final Context context) {
        if (instance == null) {
            instance = new RestApiService(context.getApplicationContext());
        }
        if (instance != null){
            Log.d("RestApiService", "--------------not null");
        }
        return instance;
    }

    public static RestApiService getInstance() {
        if (instance == null) {
            throw new IllegalStateException("RestApiService is not init!");
        }
        return instance;
    }

    public <S> S createApi(Class<S> apiClass) {
        return bayMinHttpClientRx.createApi(apiClass);
    }

    private RestApiService(final Context context) {
        bayMinHttpClientRx = new BayMinHttpClientRx();
    }
}
