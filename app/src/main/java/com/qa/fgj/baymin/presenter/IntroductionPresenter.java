package com.qa.fgj.baymin.presenter;

import com.qa.fgj.baymin.base.IBasePresenter;
import com.qa.fgj.baymin.model.IntroductionModel;
import com.qa.fgj.baymin.ui.view.IIntroductionView;

import rx.Scheduler;
import rx.Subscription;

/**
 * Created by FangGengjia on 2017/3/4.
 */

public class IntroductionPresenter<T extends IIntroductionView> implements IBasePresenter<T> {

    T mView;
    IntroductionModel mModel;

    private Scheduler executor;
    private Scheduler notifier;
    private Subscription subscription;

    public IntroductionPresenter(Scheduler executor, Scheduler notifier) {
        this.executor = executor;
        this.notifier = notifier;
        mModel = new IntroductionModel();
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void attachView(T view) {
        mView = view;
    }

    public void setmModel(IntroductionModel model){
        mModel = model;
    }

    public void fetchData(){
//        mModel.getIntroductionOnDB();
//        mModel.getIntroductionOnNet();
//        mView.
    }

    public void onRefresh(){

    }

    @Override
    public void detachView() {
        mView = null;
    }

    @Override
    public void onDestroy() {
        if (subscription != null && !subscription.isUnsubscribed()){
            subscription.unsubscribe();
        }
    }
}
