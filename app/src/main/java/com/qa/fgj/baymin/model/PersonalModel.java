package com.qa.fgj.baymin.model;

import com.qa.fgj.baymin.model.entity.BayMinResponse;
import com.qa.fgj.baymin.model.entity.UserBean;
import com.qa.fgj.baymin.net.RestApiService;
import com.qa.fgj.baymin.net.api.PersonalInfoApi;
import com.qa.fgj.baymin.util.Global;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
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

    public Observable<BayMinResponse> updateUser(UserBean userBean){
        //        RequestBody tokenBody = RequestBody.create(MediaType.parse("text/plain"), Constant.APP_TOKEN);
        RequestBody nameBody = RequestBody.create(MediaType.parse("text/plain"), userBean.getUsername());
        RequestBody emailBody = RequestBody.create(MediaType.parse("text/plain"), userBean.getEmail());
        RequestBody sexBody = RequestBody.create(MediaType.parse("text/plain"), userBean.getSex());
        File file = new File(userBean.getImagePath());
        RequestBody imageBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("imgfile", file.getName(), imageBody);
        return personalInfoApi.updateUser(part, nameBody, emailBody, sexBody);
    }

//    public Observable<BayMinResponse> synUserInfo(UserBean userBean){
//        return personalInfoApi.synUserInfo(userBean);
//    }

    public Observable<BayMinResponse> changePassword(String srcPassword, String newPassword) {
        return personalInfoApi.changePassword(srcPassword, newPassword);
    }

    public void updateLocalUser(UserBean userBean) {
        Global.userInfoDB.update(userBean);
    }
}
