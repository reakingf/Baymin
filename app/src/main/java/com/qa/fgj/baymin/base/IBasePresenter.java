package com.qa.fgj.baymin.base;

/**
 * Created by FangGengjia on 2017/1/19.
 */

public interface IBasePresenter<T extends IBaseView> {

    void onCreate();

    void attachView(T view);

    void detachView();

    void onDestroy();
}
