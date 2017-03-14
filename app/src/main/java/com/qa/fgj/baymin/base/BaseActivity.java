package com.qa.fgj.baymin.base;

import android.app.Activity;
import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.qa.fgj.baymin.R;

/**
 * Created by FangGengjia on 2017/1/19.
 */

public abstract class BaseActivity<T extends IBasePresenter> extends AppCompatActivity implements IBaseView {

//    protected void replaceFragment(int containerId, Fragment srcFragment, String tag){
//        if (null == getFragmentManager().findFragmentByTag(tag)){
//            getFragmentManager().beginTransaction().replace(R.id.fragment_container, srcFragment, tag).commit();
//        }
//    }


}
