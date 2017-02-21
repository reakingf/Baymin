package com.qa.fgj.baymin.model;

import android.content.Context;

import com.qa.fgj.baymin.util.SharPreferencesDB;

import rx.Observable;

/**
 * Created by FangGengjia on 2017/2/18.
 */

public class MainModel {

    private Context mContext;
    private SharPreferencesDB preferencesDB;

    public MainModel(Context context) {
        this.mContext = context;
        preferencesDB = new SharPreferencesDB(mContext, "loginInfo");
    }

    public Observable<Boolean> getLoginCache(){
        boolean shouldLogin = preferencesDB.getBoolean("isRemember");
        return Observable.just(shouldLogin);
    }



}
