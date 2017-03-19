package com.qa.fgj.baymin.presenter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import com.qa.fgj.baymin.base.IBasePresenter;
import com.qa.fgj.baymin.model.PersonalModel;
import com.qa.fgj.baymin.model.entity.BayMinResponse;
import com.qa.fgj.baymin.model.entity.UserBean;
import com.qa.fgj.baymin.ui.view.IPersonalInfoView;
import com.qa.fgj.baymin.util.Global;
import com.qa.fgj.baymin.util.PhotoUtils;
import com.qa.fgj.baymin.util.SystemUtil;
import com.qa.fgj.baymin.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by FangGengjia on 2017/2/19.
 */

public class PersonalInfoPresenter<T extends IPersonalInfoView> implements IBasePresenter<T> {

    private Activity activity;
    private T view;
    private PersonalModel model;

    private Scheduler uiThread;
    private Scheduler backgroundThread;

    private List<Subscription> subscriptionList = new ArrayList<>();
    private PhotoUtils photoUtils;

    public PersonalInfoPresenter(Activity activity, Scheduler uiThread, Scheduler backgroundThread) {
        this.activity = activity;
        this.uiThread = uiThread;
        this.backgroundThread = backgroundThread;
    }

    @Override
    public void onCreate() {
        model = new PersonalModel();
        photoUtils = new PhotoUtils(activity);
    }

    @Override
    public void attachView(T view) {
        this.view = view;
    }

    public void fetchData(String account) {
        Subscription subscription = model.queryByAccount(account)
                .subscribeOn(backgroundThread)
                .observeOn(uiThread)
                .subscribe(new Subscriber<UserBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.show("获取个人信息失败");
                    }

                    @Override
                    public void onNext(UserBean userBean) {
                        view.showData(userBean);
                    }
                });
        subscriptionList.add(subscription);
    }

    public void openAlbum() {
        photoUtils.openAlbum();
    }

    public Uri openCamera() {
        return photoUtils.openCamera();
    }

    public void cropPicture(Uri uri) {
        photoUtils.cropPicture(uri);
    }

    public void handleAlbumPicture(Uri uri, Subscriber<String> subscriber) {
        Subscription subscription = photoUtils.handleAlbumPicture(uri)
                .subscribeOn(backgroundThread)
                .observeOn(uiThread)
                .subscribe(subscriber);
        subscriptionList.add(subscription);

    }

    public void handleCropPicture(Uri uri, Subscriber<String> subscriber) {
        Subscription subscription = photoUtils.handleCropPicture(uri)
            .subscribeOn(backgroundThread)
            .observeOn(uiThread)
            .subscribe(subscriber);
        subscriptionList.add(subscription);
    }

    public void changePassword(String srcPassword, String newPassword, Subscriber<BayMinResponse> subscriber) {
        if (!SystemUtil.isNetworkConnected()){
            view.showError("网络不可用，请检查网络");
            return;
        }
        Subscription subscription = model.changePassword(srcPassword, newPassword)
            .subscribeOn(backgroundThread)
            .observeOn(uiThread)
            .subscribe(subscriber);
        subscriptionList.add(subscription);
    }

    public void updateLocalUser(UserBean userBean) {
        model.updateLocalUser(userBean);
    }

    /**
     * 与本地数据库和服务器同步用户信息
     */
    public void synchronizedUserInfo(UserBean user, Subscriber<BayMinResponse> subscriber) {
        if (!SystemUtil.isNetworkConnected()){
            view.showError("网络不可用，请检查网络");
            return;
        }
        updateLocalUser(user);
        Subscription subscription = model.synUserInfo(user)
                .subscribeOn(backgroundThread)
                .observeOn(uiThread)
                .subscribe(subscriber);
        subscriptionList.add(subscription);
    }

    public void logout() {
        SharedPreferences sp = activity.getSharedPreferences("loginInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("isRemember", false);
        editor.apply();
        Global.isLogin = false;
        Intent data = new Intent();
        data.putExtra("logout", true);
        activity.setResult(RESULT_OK, data);
        activity.finish();
    }

    @Override
    public void detachView() {
        view = null;
    }

    @Override
    public void onDestroy() {
        if (subscriptionList != null && subscriptionList.size() > 0){
            for (Subscription subscription : subscriptionList) {
                if (subscription != null && !subscription.isUnsubscribed()){
                    subscription.unsubscribe();
                }
            }
        }
    }

}
