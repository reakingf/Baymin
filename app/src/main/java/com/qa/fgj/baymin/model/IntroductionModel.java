package com.qa.fgj.baymin.model;

import com.qa.fgj.baymin.model.entity.IntroductionBean;
import com.qa.fgj.baymin.net.RestApiService;
import com.qa.fgj.baymin.net.api.IntroductionApi;
import com.qa.fgj.baymin.util.Global;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

/**
 * Created by FangGengjia on 2017/3/4.
 */

public class IntroductionModel {

    private IntroductionApi introductionApi;

    public IntroductionModel() {
        introductionApi = RestApiService.getInstance().createApi(IntroductionApi.class);
    }

    public Observable<IntroductionBean> getIntroductionOnNet(){
        return introductionApi.getIntroductionOnNet();
    }

    public Observable<List<IntroductionBean>> getIntroductionOnDB(){
        List<IntroductionBean> list = Global.introductionDB.load();
        if (list != null){
            return Observable.just(list);
        }
        return Observable.just(((List<IntroductionBean>)new ArrayList<IntroductionBean>()));
    }
}
