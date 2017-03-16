package com.qa.fgj.baymin.net.api;

import com.qa.fgj.baymin.model.entity.BayMinResponse;
import com.qa.fgj.baymin.model.entity.UserBean;

import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * 用户个人信息相关api
 * Created by FangGengjia on 2017/2/18.
 */

public interface PersonalInfoApi {

    @FormUrlEncoded
    @POST("BayMinServlet/RegisterServlet")
    Observable<BayMinResponse<UserBean>> register(
            @Field("nickname") String nickname,
            @Field("email") String email,
            @Field("password") String password);

    @FormUrlEncoded
    @POST("BayMinServlet/LoginServlet")
    Observable<BayMinResponse<UserBean>> login(
            @Field("email") String email,
            @Field("password") String password);

    @GET("")
    Observable<BayMinResponse<UserBean>> getUserInfo(
            @Query("email") String email);

    @FormUrlEncoded
    @POST("")
    Observable<BayMinResponse> postNewAvatar(

    );

}
