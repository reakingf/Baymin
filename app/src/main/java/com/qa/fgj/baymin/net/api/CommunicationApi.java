package com.qa.fgj.baymin.net.api;

import com.qa.fgj.baymin.model.entity.MessageBean;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by FangGengjia on 2017/1/25.
 */

public interface CommunicationApi{

    @GET("")
    Observable<MessageBean> getRespond(
            @Query("q") String question
    );


}
