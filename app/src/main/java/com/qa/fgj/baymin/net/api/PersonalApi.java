package com.qa.fgj.baymin.net.api;

import com.qa.fgj.baymin.model.entity.BayMinResponse;
import com.qa.fgj.baymin.model.entity.UserBean;

import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by FangGengjia on 2017/2/18.
 */

public interface PersonalApi {

    @FormUrlEncoded
    @POST("BayMinServlet/RegisterServlet")
    Observable<UserBean> register(@Body UserBean userBean);

    @FormUrlEncoded
    @POST("BayMinServlet/LoginServlet")
    Observable<BayMinResponse<UserBean>> login(
            @Field("email") String email,
            @Field("password") String password);



}
