package com.qa.fgj.baymin.di.module;

import android.app.Activity;
import android.app.Fragment;

import com.qa.fgj.baymin.di.FragmentScope;

import dagger.Module;
import dagger.Provides;

/**
 * Created by FangGengjia on 2017/1/19.
 */

@Module
public class FragmentModule {

    private Fragment fragment;

    public FragmentModule(Fragment fragment){
        this.fragment = fragment;
    }

    @Provides
    @FragmentScope
    public Activity provideActivity(){
        return fragment.getActivity();
    }

}
