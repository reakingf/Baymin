package com.qa.fgj.baymin.model;

import com.qa.fgj.baymin.model.entity.UserBean;
import com.qa.fgj.baymin.net.RestApiService;
import com.qa.fgj.baymin.net.api.PersonalInfoApi;
import com.qa.fgj.baymin.util.Global;

import rx.Observable;

/**
 * Created by FangGengjia on 2017/2/19.
 */

public class PersonalModel {

    private PersonalInfoApi personalInfoApi;

    public PersonalModel() {
        personalInfoApi = RestApiService.getInstance().createApi(PersonalInfoApi.class);
    }

    public Observable<UserBean> queryByAccount(String account){
        return Observable.just(Global.userInfoDB.queryByAccount(account));
    }

    public Observable<Boolean> uploadAvatar(){
        return Observable.just(true);
    }

    public Observable<Boolean> modifyUserInfo(){
        return Observable.just(true);
    }


}
