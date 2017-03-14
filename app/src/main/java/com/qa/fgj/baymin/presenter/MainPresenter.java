package com.qa.fgj.baymin.presenter;

import android.content.Context;

import com.qa.fgj.baymin.base.IBasePresenter;
import com.qa.fgj.baymin.model.MainModel;
import com.qa.fgj.baymin.model.entity.MessageBean;
import com.qa.fgj.baymin.ui.view.IMainView;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by FangGengjia on 2017/2/18.
 */

public class MainPresenter<T extends IMainView> implements IBasePresenter<T> {

    private T view;
    private MainModel mModel;
    private Context context;
    private Scheduler uiThread;
    private Scheduler backgroundThread;

    private List<Subscription> subscriptionList = new ArrayList<>();

    public MainPresenter(Context context, Scheduler uiThread, Scheduler backgroundThread) {
        this.context = context;
        this.uiThread = uiThread;
        this.backgroundThread = backgroundThread;
        this.mModel = new MainModel(this.context);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void attachView(T view) {
        this.view = view;
    }

    public void loadLoginCache(Subscriber<Boolean> subscriber){
        Subscription subscription = mModel.getLoginCache()
                .subscribeOn(backgroundThread)
                .observeOn(uiThread)
                .subscribe(subscriber);
        subscriptionList.add(subscription);
    }

    public void fetchListData(String id, int offset){
        Subscription subscription = mModel.loadData(id, offset)
                .observeOn(backgroundThread)
                .subscribeOn(uiThread)
                .subscribe(new Action1<List<MessageBean>>() {
                    @Override
                    public void call(List<MessageBean> list) {
                        view.initListViewData(list);
                    }
                });
        subscriptionList.add(subscription);
    }

    public void onRefreshing(String id, int offset, Subscriber<List> subscriber){
        Subscription subscription =  mModel.loadData(id, offset)
                .observeOn(backgroundThread)
                .subscribeOn(uiThread)
                .subscribe(subscriber);
        subscriptionList.add(subscription);
    }

    public void getAnswer(String question, Subscriber<MessageBean> subscriber){
        Subscription subscription = mModel.getAnswer(question)
                .subscribeOn(Schedulers.newThread())
                .observeOn(uiThread)
                .subscribe(subscriber);
        subscriptionList.add(subscription);
    }

    public Observable<Boolean> save(MessageBean msg){
        try{
            mModel.saveData(msg);
        } catch (Exception e){
            e.printStackTrace();
            return Observable.just(false);
        }
        return Observable.just(true);
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
