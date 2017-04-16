package com.qa.fgj.baymin.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.qa.fgj.baymin.R;
import com.qa.fgj.baymin.base.BaseActivity;
import com.qa.fgj.baymin.model.entity.weather.Index;
import com.qa.fgj.baymin.model.entity.weather.Results;
import com.qa.fgj.baymin.model.entity.weather.WeatherRoot;
import com.qa.fgj.baymin.model.entity.weather.Weather_data;
import com.qa.fgj.baymin.presenter.WeatherPresenter;
import com.qa.fgj.baymin.ui.view.IWeatherView;
import com.qa.fgj.baymin.util.ToastUtil;

import java.util.List;

/**
 * Created by FangGengjia on 2017/2/19.
 */

public class WeatherActivity extends BaseActivity implements IWeatherView {

    private ImageView iv_back;
    private TextView tv_titleTip;
    private TextView tv_operation;
    private TextView tv_temperature;
    private TextView tv_location;
    private TextView tv_weather;
    private ListView lv_futureWeather;


    public static final int REQUEST_CODE = 0xab12;

    private WeatherPresenter presenter;


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
        setContentView(R.layout.activity_weather);
        initView();
        presenter = new WeatherPresenter();
        presenter.onCreate();
        presenter.attachView(this);
        presenter.fetchData();
    }

    private void initView(){
        iv_back = (ImageView) findViewById(R.id.back);
        tv_titleTip = (TextView) findViewById(R.id.title_name);
        tv_operation = (TextView) findViewById(R.id.operation);
        tv_temperature = (TextView) findViewById(R.id.temperature);
        tv_location = (TextView) findViewById(R.id.location);
        lv_futureWeather = (ListView) findViewById(R.id.future_weather);

        tv_titleTip.setText("天气预报");
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_operation.setVisibility(View.GONE);
    }

    @Override
    public void bindData(WeatherRoot weatherRoot) {
        List<Results> weatherResults = weatherRoot.getResults();
        tv_location.setText(weatherResults.get(0).getCurrentCity());

        List<Weather_data> weather_datas = weatherResults.get(1).getWeather_data();



    }

    @Override
    public void showError(String msg) {
        ToastUtil.shortShow(msg);
    }

    @Override
    public void useNightMode(boolean isNight) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }
}
