package com.qa.fgj.baymin.net.api;

import com.qa.fgj.baymin.model.entity.BayMinResponse;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by FangGengjia on 2017/3/14.
 */

public interface AdviceQAApi {

    @FormUrlEncoded
    @POST("/QA_EXP/robot_QA_data_submit.aspx")
    Observable<BayMinResponse> postAdviceQA(
      @Field("q") String question,
      @Field("a") String answer
    );


}
