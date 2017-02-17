package com.qa.fgj.baymin.di.component;

import android.app.Activity;

import com.qa.fgj.baymin.di.ActivityScope;
import com.qa.fgj.baymin.di.module.ActivityModule;
import com.qa.fgj.baymin.ui.activity.MainActivity;

import dagger.Component;

/**
 * Created by FangGengjia on 2017/1/19.
 */

@ActivityScope
@Component(dependencies = AppComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    Activity getActivity();

    void inject(MainActivity mainActivity);

    //todo 待添加
}
