package com.qa.fgj.baymin.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.qa.fgj.baymin.base.BaseActivity;
import com.qa.fgj.baymin.presenter.WeatherPresenter;
import com.qa.fgj.baymin.ui.view.IWetherView;

/**
 * Created by FangGengjia on 2017/2/19.
 */

public class WeatherActivity extends BaseActivity<WeatherPresenter> implements IWetherView {

    public static final int REQUEST_CODE = 0xab12;

    public static void start(final Context context){
        Intent intent = new Intent(context, WeatherActivity.class);
        context.startActivity(intent);
    }

    public static void startForResult(final Activity activity){
        Intent intent = new Intent(activity, WeatherActivity.class);
        activity.startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
