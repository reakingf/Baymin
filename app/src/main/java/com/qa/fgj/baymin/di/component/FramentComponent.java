package com.qa.fgj.baymin.di.component;

import android.app.Activity;

import com.qa.fgj.baymin.di.FragmentScope;
import com.qa.fgj.baymin.di.module.FragmentModule;
import com.qa.fgj.baymin.ui.fragment.CommunicationFragment;
import com.qa.fgj.baymin.ui.activity.IntroductionActivity;

import dagger.Component;

/**
 * Created by FangGengjia on 2017/1/19.
 */

@FragmentScope
@Component(dependencies = AppComponent.class, modules = FragmentModule.class)
public interface FramentComponent {

    Activity getActivity();

    void inject(CommunicationFragment communicationFragment);

    void inject(IntroductionActivity introductionActivity);

    //todo 待添加

}
