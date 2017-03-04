package com.qa.fgj.baymin.ui.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.qa.fgj.baymin.R;
import com.qa.fgj.baymin.presenter.IntroductionPresenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * todo 数据考虑从后台和本地数据库获取，后期改进
 * Created by FangGengjia on 2017/1/18.
 */

public class IntroductionFragment extends Fragment {

    public static final String TAG = IntroductionFragment.class.getSimpleName();

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
    private AlertDialog.Builder dialog;
    private List<String> introInfo = new ArrayList<>();

    IntroductionPresenter presenter;
    private Scheduler executor = AndroidSchedulers.mainThread();
    private Scheduler notifier = Schedulers.io();

    public static IntroductionFragment newInstance(){
        return new IntroductionFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new IntroductionPresenter(executor, notifier);
        presenter.fetchData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_introduction, container, false);
        ListView listView = (ListView) view.findViewById(R.id.introduction_listView);
        bindData();
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);
        dialog = new AlertDialog.Builder(getActivity());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialog.setTitle(list.get(position));
                dialog.setMessage(introInfo.get(position));
                dialog.setCancelable(true);
                dialog.setNegativeButton(getString(R.string.back), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.show();
            }
        });
        return view;
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

}
