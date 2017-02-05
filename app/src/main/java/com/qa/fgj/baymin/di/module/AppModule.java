package com.qa.fgj.baymin.di.module;

import com.qa.fgj.baymin.app.App;

import dagger.Module;

/**
 * Created by FangGengjia on 2017/1/19.
 */

@Module
public class AppModule {

    private final App application;

    public AppModule(App application) {
        this.application = application;
    }


}
