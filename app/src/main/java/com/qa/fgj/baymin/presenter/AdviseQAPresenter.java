package com.qa.fgj.baymin.presenter;

import com.qa.fgj.baymin.base.IBasePresenter;
import com.qa.fgj.baymin.model.AdviseQAModel;
import com.qa.fgj.baymin.ui.view.IAdviseQAView;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 *
 * Created by FangGengjia on 2017/3/14.
 */

public class AdviseQAPresenter<T extends IAdviseQAView> implements IBasePresenter<T> {

    private T view;
    private AdviseQAModel model;

    private Subscription subscription;

    @Override
    public void onCreate() {
        model = new AdviseQAModel();
    }

    @Override
    public void attachView(T view) {
        this.view = view;
    }

    public void submit(String question, String answer, Subscriber<Boolean> subscriber){
        subscription =model.adviceQA(question, answer)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    @Override
    public void detachView() {
        view = null;
    }

    @Override
    public void onDestroy() {
        if (subscription != null && !subscription.isUnsubscribed()){
            subscription.unsubscribe();
        }
    }
}
