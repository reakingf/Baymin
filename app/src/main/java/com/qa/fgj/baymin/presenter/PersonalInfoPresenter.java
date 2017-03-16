package com.qa.fgj.baymin.presenter;

import com.qa.fgj.baymin.base.IBasePresenter;
import com.qa.fgj.baymin.base.IBaseView;
import com.qa.fgj.baymin.model.PersonalModel;
import com.qa.fgj.baymin.model.entity.UserBean;
import com.qa.fgj.baymin.ui.view.IPersonalInfoView;
import com.qa.fgj.baymin.util.Global;
import com.qa.fgj.baymin.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
import rx.schedulers.Schedulers;

/**
 * Created by FangGengjia on 2017/2/19.
 */

public class PersonalInfoPresenter<T extends IPersonalInfoView> implements IBasePresenter<T> {

    private T view;
    private PersonalModel model;

    private Scheduler uiThread;
    private Scheduler backgroundThread;

    private List<Subscription> subscriptionList = new ArrayList<>();

    public PersonalInfoPresenter(Scheduler uiThread, Scheduler backgroundThread) {
        this.uiThread = uiThread;
        this.backgroundThread = backgroundThread;
    }

    @Override
    public void onCreate() {
        model = new PersonalModel();
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
