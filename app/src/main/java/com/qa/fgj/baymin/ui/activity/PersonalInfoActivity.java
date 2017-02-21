package com.qa.fgj.baymin.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;

import com.qa.fgj.baymin.base.BaseActivity;
import com.qa.fgj.baymin.presenter.PersonalInfoPresenter;
import com.qa.fgj.baymin.ui.activity.view.IPersonalInfoView;

/**
 * Created by FangGengjia on 2017/2/19.
 */

public class PersonalInfoActivity extends BaseActivity<PersonalInfoPresenter> implements IPersonalInfoView {

    public static final int REQUEST_CODE = 0xaa12;

    public static void start(final Context context){
        Intent intent = new Intent(context, PersonalInfoActivity.class);
        context.startActivity(intent);
    }

    public static void startForResult(final Activity activity){
        Intent intent = new Intent(activity, PersonalInfoActivity.class);
        activity.startActivityForResult(intent, REQUEST_CODE);
    }


    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

    }




    @Override
    public void showError(String msg) {

    }

    @Override
    public void useNightMode(boolean isNight) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
