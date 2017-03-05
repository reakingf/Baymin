package com.qa.fgj.baymin.net.api;

import com.qa.fgj.baymin.model.entity.IntroductionBean;

import retrofit2.http.GET;
import rx.Observable;

/**
 * 用于获取小白说明
 * Created by FangGengjia on 2017/3/5.
 */

public interface IntroductionApi {

    @GET("/")
    Observable<IntroductionBean> getIntroductionOnNet();

}
