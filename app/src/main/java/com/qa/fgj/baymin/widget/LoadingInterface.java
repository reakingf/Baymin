package com.qa.fgj.baymin.widget;

/**
 * Created by FangGengjia on 2017/4/11.
 */

public interface LoadingInterface {

    void onLoadingData();
    void onReloadData();
    void onLoadingError();
    void onLoadingEmpty();
    void onNetworkError();
    void onFirstLoadingToast();
    void setEmptyMessageTip(String message);
    void setErrorMessageTip(String message);
    void onFinishLoading();

}
