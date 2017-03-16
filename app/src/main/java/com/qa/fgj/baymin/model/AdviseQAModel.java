package com.qa.fgj.baymin.model;

import com.qa.fgj.baymin.model.entity.BayMinResponse;
import com.qa.fgj.baymin.net.RestApiService;
import com.qa.fgj.baymin.net.api.AdviceQAApi;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by FangGengjia on 2017/3/14.
 */

public class AdviseQAModel {

    private final AdviceQAApi adviceQAApi;

    public AdviseQAModel() {
        adviceQAApi = RestApiService.getInstance().createApi(AdviceQAApi.class);
    }

    public Observable<Boolean> adviceQA(String question, String answer){
        return adviceQAApi.postAdviceQA(question, answer)
                .map(new Func1<BayMinResponse, Boolean>() {
                    @Override
                    public Boolean call(BayMinResponse response) {
                        return response.isSucceed();
                    }
                });
    }

}
