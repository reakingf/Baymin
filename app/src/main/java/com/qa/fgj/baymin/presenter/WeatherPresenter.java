package com.qa.fgj.baymin.presenter;

import com.qa.fgj.baymin.base.IBasePresenter;
import com.qa.fgj.baymin.base.IBaseView;
import com.qa.fgj.baymin.model.WeatherModel;
import com.qa.fgj.baymin.model.entity.weather.WeatherRoot;
import com.qa.fgj.baymin.ui.view.IWeatherView;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.Subscription;

/**
 * Created by FangGengjia on 2017/2/19.
 */

public class WeatherPresenter implements IWeatherPresenter<IWeatherView> {

    private IWeatherView view;
    private WeatherModel model;
    private List<Subscription> subscriptionList = new ArrayList<>();

    @Override
    public void onCreate() {
        model = new WeatherModel();
    }

    @Override
    public void attachView(IWeatherView view) {
        this.view = view;
    }

    public void fetchData() {

        Subscriber<WeatherRoot> subscriber = new Subscriber<WeatherRoot>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                view.showError(e.getMessage() == null ? "获取天气信息失败" : e.getMessage());
            }

            @Override
            public void onNext(WeatherRoot weatherRoot) {
                view.bindData(weatherRoot);
            }
        };

        Subscription subscription = model.getWeatherData()
                .subscribe(subscriber);
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
