package com.qa.fgj.baymin.model;


import com.qa.fgj.baymin.model.entity.UserBean;
import com.qa.fgj.baymin.net.RestApiService;
import com.qa.fgj.baymin.net.api.PersonalApi;

import rx.Observable;

/**
 * Created by FangGengjia on 2017/2/18.
 */

public class RegisterModel {

    private PersonalApi personalApi;

    public RegisterModel() {
        personalApi = RestApiService.getInstance().createApi(PersonalApi.class);
    }

    public Observable<UserBean> signUp(UserBean userBean){
        return personalApi.register(userBean);
    }

}
