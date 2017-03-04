package com.qa.fgj.baymin.presenter;

import com.qa.fgj.baymin.base.IBasePresenter;
import com.qa.fgj.baymin.model.CommunicationModel;
import com.qa.fgj.baymin.model.entity.MessageBean;
import com.qa.fgj.baymin.ui.activity.view.ICommunicationView;

import java.util.List;

import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.functions.Action1;

/**
 * Created by FangGengjia on 2017/1/29
 */

public class CommunicationPresenter<T extends ICommunicationView> implements IBasePresenter<T> {

    private T mView;
    private CommunicationModel mModel;
    private Scheduler executor;
    private Scheduler notifier;

    public CommunicationPresenter(Scheduler executor, Scheduler notifier) {
        this.executor = executor;
        this.notifier = notifier;
    }

    @Override
    public void attachView(T view) {
        mView = view;
    }

    public void setModel(CommunicationModel model){
        mModel = model;
    }

    public void fetch(String id, int offset){
        mModel.loadData(id, offset)
            .observeOn(notifier)
            .subscribeOn(executor)
            .subscribe(new Action1<List<MessageBean>>() {
            @Override
            public void call(List<MessageBean> list) {
                mView.initListViewData(list);
            }
        });
    }

    public void onRefreshing(String id, int offset, Subscriber<List<MessageBean>> subscriber){
        mModel.loadData(id, offset)
            .observeOn(notifier)
            .subscribeOn(executor)
            .subscribe(subscriber);
    }

    public void getAnswer(String question, Subscriber<MessageBean> subscriber){
        mModel.getAnswer(question)
            .observeOn(notifier)
            .subscribeOn(executor)
            .subscribe(subscriber);
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
        mView = null;
    }
}
