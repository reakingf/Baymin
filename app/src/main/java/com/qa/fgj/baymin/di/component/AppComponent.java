package com.qa.fgj.baymin.di.component;

import com.qa.fgj.baymin.app.App;
import com.qa.fgj.baymin.di.module.AppModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by FangGengjia on 2017/1/19.
 */

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {

    // 提供App的Context
    App getContext();

    //提供http的帮助类
//    RetrofitHelper retrofitHelper();

}
