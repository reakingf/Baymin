package com.qa.fgj.baymin.model;

import com.qa.fgj.baymin.model.entity.UserBean;
import com.qa.fgj.baymin.net.RestApiService;
import com.qa.fgj.baymin.net.api.PersonalApi;
import com.qa.fgj.baymin.util.Global;

import rx.Observable;

/**
 * Created by FangGengjia on 2017/2/18.
 */

public class LoginModel {

    private PersonalApi personalApi;

    public LoginModel(PersonalApi personalApi) {
        this.personalApi = RestApiService.getInstance().createApi(PersonalApi.class);
    }

    public Observable<UserBean> login(UserBean userBean){
        return personalApi.login(userBean);
    }

    public UserBean getUserByEmail(String email){
        return Global.userInfoDB.queryByEmail(email);
    }

    public void saveUser(UserBean user){
        Global.userInfoDB.saveOrUpdate(user);
    }

    public void saveOrUpdateUser(UserBean user){
        Global.userInfoDB.saveOrUpdate(user);
    }

}
