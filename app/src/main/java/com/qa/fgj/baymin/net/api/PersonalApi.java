package com.qa.fgj.baymin.net.api;

import com.qa.fgj.baymin.model.entity.UserBean;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by FangGengjia on 2017/2/18.
 */

public interface PersonalApi {

    @POST("")
    Observable<UserBean> register(@Body UserBean userBean);

    @POST("")
    Observable<UserBean> login(@Body UserBean userBean);

}
