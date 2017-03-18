package com.qa.fgj.baymin.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.qa.fgj.baymin.R;
import com.qa.fgj.baymin.base.BaseActivity;
import com.qa.fgj.baymin.presenter.IntroductionPresenter;
import com.qa.fgj.baymin.ui.view.IIntroductionView;
import com.qa.fgj.baymin.util.ToastUtil;
import com.qa.fgj.baymin.widget.ShowTipDialog;

import java.util.ArrayList;
import java.util.List;

import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * todo 数据考虑从后台和本地数据库获取，后期改进
 * Created by FangGengjia on 2017/1/18.
 */

public class IntroductionActivity extends BaseActivity implements IIntroductionView {

    private final static int DAILY_TALK = 0;
    private final static int MUSIC = 1;
    private final static int TIME = 2;
    private final static int WEATHER = 3;
    private final static int POETRY = 4;
    private final static int STORY = 5;
    private final static int TRANSLATE = 6;
    private final static int BAI_KE = 7;
    private final static int CALCULATE = 8;
    private final static int LEARNING = 9;
    private final static int VOICE_WAKEUP = 10;
    private final static int LANGUAGE_SWITCH = 11;
    private final static int VERSION_UPDATE = 12;
    private final static int ACTION = 13;

    ArrayAdapter<String> adapter;
    private List<String> list = new ArrayList<>();
    private ShowTipDialog dialog;
    private List<String> introInfo = new ArrayList<>();

    IntroductionPresenter presenter;
    private Scheduler uiThread = AndroidSchedulers.mainThread();
    private Scheduler backgroundThread = Schedulers.io();

    public static void start(Context context){
        Intent intent = new Intent(context, IntroductionActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduction);
        ListView listView = (ListView) findViewById(R.id.introduction_listView);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.introduction));
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        bindData();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialog = new ShowTipDialog(IntroductionActivity.this);
                dialog.setTitleText(list.get(position));
                dialog.setContentGravity(Gravity.LEFT);
                dialog.setContentText(introInfo.get(position));
                dialog.setSingleButton(true);
                dialog.show();
            }
        });
        presenter = new IntroductionPresenter(uiThread, backgroundThread);
        presenter.fetchData();
    }

    public void bindData(){
        list.add(getString(R.string.daily_title));
        list.add(getString(R.string.music_title));
        list.add(getString(R.string.time_title));
        list.add(getString(R.string.weather_title));
        list.add(getString(R.string.poetry_title));
        list.add(getString(R.string.story_title));
        list.add(getString(R.string.translate_title));
        list.add(getString(R.string.bai_ke_title));
        list.add(getString(R.string.calculate_title));
        list.add(getString(R.string.learn_title));
        list.add(getString(R.string.wake_up_title));
        list.add(getString(R.string.lang_switch_title));
        list.add(getString(R.string.update_title));
        list.add(getString(R.string.action_title));
        introInfo.add(DAILY_TALK, getString(R.string.intro_daily));
        introInfo.add(MUSIC, getString(R.string.intro_music));
        introInfo.add(TIME, getString(R.string.intro_time));
        introInfo.add(WEATHER, getString(R.string.intro_weather));
        introInfo.add(POETRY, getString(R.string.intro_poetry));
        introInfo.add(STORY, getString(R.string.intro_story));
        introInfo.add(TRANSLATE, getString(R.string.intro_translate));
        introInfo.add(BAI_KE, getString(R.string.intro_baike));
        introInfo.add(CALCULATE, getString(R.string.intro_calculate));
        introInfo.add(LEARNING, getString(R.string.intro_learning));
        introInfo.add(VOICE_WAKEUP, getString(R.string.intro_wakeup));
        introInfo.add(LANGUAGE_SWITCH, getString(R.string.intro_lang_switch));
        introInfo.add(VERSION_UPDATE, getString(R.string.intro_update));
        introInfo.add(ACTION, getString(R.string.intro_action));
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
        if (dialog != null && dialog.isShowing()){
            dialog.dismiss();
        }
        presenter.detachView();
        presenter.onDestroy();
    }
}
