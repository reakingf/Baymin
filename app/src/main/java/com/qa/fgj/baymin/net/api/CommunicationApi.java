package com.qa.fgj.baymin.net.api;

import com.qa.fgj.baymin.model.entity.BayMinResponse;
import com.qa.fgj.baymin.model.entity.MessageBean;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * 聊天
 * Created by FangGengjia on 2017/1/25.
 */

public interface CommunicationApi{

    @GET("/BayMinServlet/Communication")
    Observable<BayMinResponse<String>> getRespond(
            @Query("q") String question);


}
