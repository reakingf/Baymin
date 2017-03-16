package com.qa.fgj.baymin.model;

import com.qa.fgj.baymin.net.RestApiService;
import com.qa.fgj.baymin.net.api.PersonalApi;

import rx.Observable;

/**
 * Created by FangGengjia on 2017/2/19.
 */

public class PersonalModel {

    private PersonalApi personalApi;

    public PersonalModel() {
        personalApi = RestApiService.getInstance().createApi(PersonalApi.class);
    }

    Observable<Boolean> uploadAvatar(){
        return Observable.just(true);
    }

    Observable<Boolean> modifyUserInfo(){
        return Observable.just(true);
    }


}
