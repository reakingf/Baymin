package com.qa.fgj.baymin.presenter;

import com.qa.fgj.baymin.base.IBasePresenter;
import com.qa.fgj.baymin.model.CommunicationModel;
import com.qa.fgj.baymin.model.entity.MessageBean;
import com.qa.fgj.baymin.ui.activity.ICommunicationView;

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

    public void fetch(String id, int offset){
        mModel.loadData(id, offset)
                .observeOn(executor)
        .subscribeOn(notifier)
        .subscribe(new Action1<List<MessageBean>>() {
            @Override
            public void call(List<MessageBean> messageBeen) {
                mView.initListViewData(messageBeen);
            }
        });
    }

    public void getAnswer(String question, Subscriber subscriber){
        mModel.getAnswer(question)
        .observeOn(executor)
        .subscribeOn(notifier)
        .subscribe(subscriber);
    }

    //todo 发送成功才保存？
    public Observable<Boolean> save(MessageBean msg, Subscriber subscriber){
        mModel.saveData(msg);
        return Observable.just(true);
    }

    @Override
    public void detachView() {

    }
}
