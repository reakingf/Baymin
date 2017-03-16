package com.qa.fgj.baymin.model;

import android.util.Log;

import com.qa.fgj.baymin.model.entity.BayMinResponse;
import com.qa.fgj.baymin.model.entity.UserBean;
import com.qa.fgj.baymin.net.RestApiService;
import com.qa.fgj.baymin.net.api.PersonalApi;
import com.qa.fgj.baymin.util.Global;
import com.qa.fgj.baymin.util.LogUtil;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by FangGengjia on 2017/2/18.
 */

public class LoginModel {

    private PersonalApi personalApi;

    public LoginModel() {
        this.personalApi = RestApiService.getInstance().createApi(PersonalApi.class);
    }

    public Observable<UserBean> login(UserBean userBean){
        return personalApi.login(userBean.getEmail(), userBean.getPassword())
                .map(new Func1<BayMinResponse<UserBean>, UserBean>() {
                    @Override
                    public UserBean call(BayMinResponse<UserBean> response) {
                        Log.d("login", "------------" + response.toString());
                        return response.getContent();
                    }
                });
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
