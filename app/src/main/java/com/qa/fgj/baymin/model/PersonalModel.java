package com.qa.fgj.baymin.model;

import com.qa.fgj.baymin.net.RestApiService;
import com.qa.fgj.baymin.net.api.PersonalInfoApi;

import rx.Observable;

/**
 * Created by FangGengjia on 2017/2/19.
 */

public class PersonalModel {

    private PersonalInfoApi personalInfoApi;

    public PersonalModel() {
        personalInfoApi = RestApiService.getInstance().createApi(PersonalInfoApi.class);
    }

    Observable<Boolean> uploadAvatar(){
        return Observable.just(true);
    }

    Observable<Boolean> modifyUserInfo(){
        return Observable.just(true);
    }


}
