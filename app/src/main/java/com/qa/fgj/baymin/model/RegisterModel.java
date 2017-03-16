package com.qa.fgj.baymin.model;


import com.qa.fgj.baymin.model.entity.UserBean;
import com.qa.fgj.baymin.net.RestApiService;
import com.qa.fgj.baymin.net.api.PersonalInfoApi;

import rx.Observable;

/**
 * Created by FangGengjia on 2017/2/18.
 */

public class RegisterModel {

    private PersonalInfoApi personalInfoApi;

    public RegisterModel() {
        personalInfoApi = RestApiService.getInstance().createApi(PersonalInfoApi.class);
    }

    public Observable<UserBean> signUp(UserBean userBean){
        return personalInfoApi.register(userBean);
    }

}
