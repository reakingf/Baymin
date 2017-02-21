package com.qa.fgj.baymin.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qa.fgj.baymin.R;
import com.qa.fgj.baymin.base.BaseActivity;
import com.qa.fgj.baymin.presenter.MainPresenter;
import com.qa.fgj.baymin.ui.activity.view.IMainView;
import com.qa.fgj.baymin.ui.fragment.CommunicationFragment;
import com.qa.fgj.baymin.ui.fragment.IntroductionFragment;
import com.qa.fgj.baymin.widget.RoundImageView;
import com.squareup.haha.perflib.Main;

import rx.Scheduler;
import rx.Subscriber;

public class MainActivity extends BaseActivity<MainPresenter> implements IMainView, View.OnClickListener,
        NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawer;
    Toolbar toolbar;
//    FloatingActionButton fab;
    NavigationView navigationView;
    LinearLayout withoutLoginLayout;
    TextView loginButton;
    LinearLayout loggedInLayout;
    RoundImageView userFace;
    TextView userName;
    TextView growthValue;
    TextView temperature;
    TextView place;
    AlertDialog.Builder builder;

    CommunicationFragment mCommunicationFragment;
    IntroductionFragment mIntroductionFragment;
    //...

    private MainPresenter mPresenter;
    private Scheduler executor;
    private Scheduler notifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        mPresenter = new MainPresenter(this, executor, notifier);
        mPresenter.attachView(this);
        checkShouldLogin();
        if (savedInstanceState == null) {
            replaceFragment(R.id.fragment_container, CommunicationFragment.newInstance(), CommunicationFragment.TAG);
        } else {
            //TODO 从缓存中获取fragment
        }
    }

    private void initView() {
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View heardView = navigationView.getHeaderView(0);
        withoutLoginLayout = (LinearLayout) findViewById(R.id.header_without_login);
        loginButton = (TextView) heardView.findViewById(R.id.header_to_login);
        loggedInLayout = (LinearLayout) findViewById(R.id.header_already_login);
        userFace = (RoundImageView) heardView.findViewById(R.id.face);
        userName = (TextView) heardView.findViewById(R.id.user_name);
        growthValue = (TextView) heardView.findViewById(R.id.growth);
        temperature = (TextView) heardView.findViewById(R.id.temperature);
        place = (TextView) heardView.findViewById(R.id.place);
        setListener();
    }

    public void setListener(){
//        fab.setOnClickListener(new View.OnClickListener() {
            //            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        loginButton.setOnClickListener(this);
        userFace.setOnClickListener(this);
        temperature.setOnClickListener(this);
        place.setOnClickListener(this);
    }

    public void checkShouldLogin(){
        Subscriber<Boolean> subscriber = new Subscriber<Boolean>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Boolean aBoolean) {
                if (aBoolean){
                    LoginActivity.startForResult(MainActivity.this);
                }
            }
        };
        mPresenter.loadLoginCache(subscriber);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.setVoiceLanguage:
                setVoiceLanguage();
                break;
            case R.id.recognizeWakeUp:
//                setASRWakeUp();
                break;
            case R.id.helpBayMin:
//                Intent intent = new Intent(MainActivity.this, HelpLearningActivity.class);
//                startActivity(intent);
                break;
            case R.id.setBackground:
//                Toast.makeText(thisContext.getApplicationContext(), "待完善", Toast.LENGTH_LONG).show();
                break;
            case R.id.introduction:
//                Intent intent1 = new Intent(MainActivity.this, IntroductionActivity.class);
//                startActivity(intent1);
                break;
            case R.id.checkVersion:
//                updateManager = new UpdateManager(thisContext);
//                updateManager.isUpdate();
                break;
            case R.id.setLanguage:
//                setSystemLanguage();
                break;
            case R.id.exit:
//                setExitApp();
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    /**
     * 设置语音输入类型
     */
    public void setVoiceLanguage() {
        builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.setVoiceLanguageTitle);
        final String[] languageTypes = {"普通话(中国)", "English(United State)"};
        builder.setItems(languageTypes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                config.setCurrentLanguage(which);
                String tips = getResources().getString(R.string.currentLanguage) +
                        languageTypes[which];
                Toast.makeText(getApplicationContext(), tips, Toast.LENGTH_LONG).show();
            }
        });
        builder.setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    @Override
    public void showError(String msg) {

    }

    @Override
    public void useNightMode(boolean isNight) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.header_to_login:
                LoginActivity.startForResult(MainActivity.this);
            break;
            case R.id.user_face_img:
                PersonalInfoActivity.startForResult(MainActivity.this);
                break;
            case R.id.temperature:
            case R.id.place:
                WeatherActivity.startForResult(MainActivity.this);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
