package com.qa.fgj.baymin.di.module;

import android.app.Activity;

import com.qa.fgj.baymin.di.ActivityScope;

import dagger.Module;
import dagger.Provides;

/**
 * Created by FangGengjia on 2017/1/19.
 */

@Module
public class ActivityModule {

    private Activity activity;

    public ActivityModule(Activity activity){
        this.activity = activity;
    }

    @Provides
    @ActivityScope
    public Activity provideActivity(){
        return activity;
    }

}
