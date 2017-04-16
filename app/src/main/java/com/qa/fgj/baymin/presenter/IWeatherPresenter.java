package com.qa.fgj.baymin.presenter;

/**
 * Created by FangGengjia on 2017/4/12.
 */

public interface IWeatherPresenter<T>{

    void onCreate();

    void attachView(T view);

    void detachView();

    void onDestroy();
}
