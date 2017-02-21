package com.qa.fgj.baymin.presenter;

import android.content.Context;

import com.qa.fgj.baymin.base.IBasePresenter;
import com.qa.fgj.baymin.model.MainModel;
import com.qa.fgj.baymin.ui.activity.view.IMainView;

import rx.Scheduler;
import rx.Subscriber;

/**
 * Created by FangGengjia on 2017/2/18.
 */

public class MainPresenter<T extends IMainView> implements IBasePresenter<T> {

    private T mView;
    private MainModel mModel;
    private Context mContext;
    private Scheduler mExecutor;
    private Scheduler mNotifier;

    public MainPresenter(Context context, Scheduler executor, Scheduler notifier) {
        mContext = context;
        mExecutor = executor;
        mNotifier = notifier;
        mModel = new MainModel(mContext);
    }

    @Override
    public void attachView(T view) {
        mView = view;
    }

    public void loadLoginCache(Subscriber<Boolean> subscriber){
        mModel.getLoginCache()
                .subscribeOn(mNotifier)
                .observeOn(mExecutor)
                .subscribe(subscriber);
    }

    public void bindData(){

    }

    @Override
    public void detachView() {
        mView = null;
    }
}
