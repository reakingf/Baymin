package com.qa.fgj.baymin.model;

import com.qa.fgj.baymin.model.entity.BayMinResponse;
import com.qa.fgj.baymin.model.entity.UserBean;
import com.qa.fgj.baymin.net.RestApiService;
import com.qa.fgj.baymin.net.api.PersonalInfoApi;
import com.qa.fgj.baymin.util.Global;

import okhttp3.ResponseBody;
import rx.Observable;

/**
 * Created by FangGengjia on 2017/2/18.
 */

public class LoginModel {

    private PersonalInfoApi personalInfoApi;

    public LoginModel() {
        this.personalInfoApi = RestApiService.getInstance().createApi(PersonalInfoApi.class);
    }

    public Observable<BayMinResponse<UserBean>> login(String email, String password){
        return personalInfoApi.login(email, password);
    }

    public Observable<ResponseBody> syncAvatar(String email){
        return personalInfoApi.downLoadAvatar(email);
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
