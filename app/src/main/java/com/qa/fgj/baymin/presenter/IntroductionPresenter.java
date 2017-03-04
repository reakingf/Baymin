package com.qa.fgj.baymin.presenter;

import com.qa.fgj.baymin.base.IBasePresenter;
import com.qa.fgj.baymin.model.IntroductionModel;
import com.qa.fgj.baymin.ui.activity.view.IIntroductionView;

import java.util.ArrayList;
import java.util.List;

import rx.Scheduler;

/**
 * Created by FangGengjia on 2017/3/4.
 */

public class IntroductionPresenter<T extends IIntroductionView> implements IBasePresenter<T> {

    T mView;
    IntroductionModel mModel;

    private Scheduler executor;
    private Scheduler notifier;

    public IntroductionPresenter(Scheduler executor, Scheduler notifier) {
        this.executor = executor;
        this.notifier = notifier;
        mModel = new IntroductionModel();
    }

    @Override
    public void attachView(T view) {
        mView = view;
    }

    public void setmModel(IntroductionModel model){
        mModel = model;
    }

    public void fetchData(){

    }

    @Override
    public void detachView() {
        mView = null;
    }
}
