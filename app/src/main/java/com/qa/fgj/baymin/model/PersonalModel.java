package com.qa.fgj.baymin.model;

import com.qa.fgj.baymin.model.entity.BayMinResponse;
import com.qa.fgj.baymin.model.entity.UserBean;
import com.qa.fgj.baymin.net.RestApiService;
import com.qa.fgj.baymin.net.api.PersonalInfoApi;
import com.qa.fgj.baymin.util.Global;

import rx.Observable;
import rx.functions.Func1;

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

    public Observable<BayMinResponse> synUserInfo(UserBean userBean){
        return personalInfoApi.synUserInfo(userBean);
    }

    public Observable<BayMinResponse> changePassword(String srcPassword, String newPassword) {
        return personalInfoApi.changePassword(srcPassword, newPassword);
    }

    public void updateLocalUser(UserBean userBean) {
        Global.userInfoDB.update(userBean);
    }
}
