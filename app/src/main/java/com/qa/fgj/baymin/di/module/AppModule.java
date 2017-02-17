package com.qa.fgj.baymin.di.module;

import com.qa.fgj.baymin.app.App;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by FangGengjia on 2017/1/19.
 */

@Module
public class AppModule {

    private final App application;

    public AppModule(App application) {
        this.application = application;
    }

    @Provides
    @Singleton
    App provideApplicationContext(){
        return application;
    }

}
