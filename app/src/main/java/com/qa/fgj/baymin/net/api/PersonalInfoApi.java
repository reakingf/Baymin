package com.qa.fgj.baymin.net.api;

import com.qa.fgj.baymin.model.entity.BayMinResponse;
import com.qa.fgj.baymin.model.entity.UserBean;

import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by FangGengjia on 2017/3/15.
 */

public interface PersonalInfoApi {

    @GET("")
    Observable<BayMinResponse<UserBean>> getUserInfo(
        @Query("email") String email);

    @FormUrlEncoded
    @POST("")
    Observable<BayMinResponse> postNewAvatar(

    );

}
