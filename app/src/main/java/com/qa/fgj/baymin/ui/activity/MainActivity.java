package com.qa.fgj.baymin.ui.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qa.fgj.baymin.R;
import com.qa.fgj.baymin.base.BaseActivity;
import com.qa.fgj.baymin.model.entity.MessageBean;
import com.qa.fgj.baymin.model.entity.UserBean;
import com.qa.fgj.baymin.presenter.MainPresenter;
import com.qa.fgj.baymin.ui.view.IMainView;
import com.qa.fgj.baymin.ui.adapter.MsgAdapter;
import com.qa.fgj.baymin.util.Global;
import com.qa.fgj.baymin.util.LogUtil;
import com.qa.fgj.baymin.util.ToastUtil;
import com.qa.fgj.baymin.widget.RoundImageView;
import com.qa.fgj.baymin.widget.SpeechRecognizeDialog;
import com.qa.fgj.baymin.widget.XListView;

import java.util.ArrayList;
import java.util.List;

import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity<MainPresenter> implements IMainView, View.OnClickListener,
        NavigationView.OnNavigationItemSelectedListener, XListView.IXListViewListener {

    DrawerLayout drawer;
    Toolbar toolbar;
    NavigationView navigationView;
    LinearLayout withoutLoginLayout;
    TextView loginButton;
    LinearLayout loggedInLayout;
    RoundImageView userFace;
    TextView userName;
    TextView growthValue;
    TextView temperature;
    TextView place;

    XListView listView;
    LinearLayout inputLayout;
    ImageButton inputType;
    Button voiceButton;
    EditText editText;
    Button sendButton;

    AlertDialog.Builder builder;

    private SpeechRecognizeDialog dialog;
    private List<MessageBean> listData;
    private MsgAdapter adapter;
    /* 初始消息id */
    private String msgID = "0";
    private boolean mPullRefreshing = false;

    private InputMethodManager inputMethodManager;
    private long exitTime = 0;

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!"".equals(editText.getText().toString().trim())){
                sendButton.setBackgroundResource(R.drawable.bg_button_green);
            } else {
                sendButton.setBackgroundResource(R.drawable.bg_button);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    private MainPresenter presenter;
    private Scheduler uiThread = AndroidSchedulers.mainThread();
    private Scheduler backgroundThread = Schedulers.io();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        presenter = new MainPresenter(this, uiThread, backgroundThread);
        presenter.onCreate();
        presenter.attachView(this);
        checkShouldLogin();
    }

    private void initView() {
        setSupportActionBar(toolbar);
        initDrawerLayout();
        inputLayout = (LinearLayout) findViewById(R.id.msgButtonBar);
        inputType = (ImageButton) findViewById(R.id.inputType);
        voiceButton = (Button) findViewById(R.id.voiceButton);
        editText = (EditText) findViewById(R.id.textInput);
        sendButton = (Button) findViewById(R.id.sendButton);
        initListView();
        setListener();
    }

    private void initDrawerLayout() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View heardView = navigationView.getHeaderView(0);
        withoutLoginLayout = (LinearLayout) heardView.findViewById(R.id.header_without_login);
        loginButton = (TextView) heardView.findViewById(R.id.header_to_login);
        loggedInLayout = (LinearLayout) heardView.findViewById(R.id.header_already_login);
        userFace = (RoundImageView) heardView.findViewById(R.id.face);
        userName = (TextView) heardView.findViewById(R.id.user_name);
        growthValue = (TextView) heardView.findViewById(R.id.growth);
        temperature = (TextView) heardView.findViewById(R.id.temperature);
        place = (TextView) heardView.findViewById(R.id.place);

        toolbar.setTitle(R.string.app_name);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
    }

    private void initListView() {
        listView = (XListView) findViewById(R.id.listContent);
        listView.setPullLoadEnable(false);
        listData = new ArrayList<>();
        adapter = new MsgAdapter(this, R.layout.item_chat_list, listData);
        listView.setAdapter(adapter);
        listView.setXListViewListener(this);
    }

    public void setListener(){
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        loginButton.setOnClickListener(this);
        userFace.setOnClickListener(this);
        temperature.setOnClickListener(this);
        place.setOnClickListener(this);
        inputType.setOnClickListener(this);
        voiceButton.setOnClickListener(this);
        sendButton.setOnClickListener(this);
        editText.addTextChangedListener(mTextWatcher);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            switch (requestCode){
                case LoginActivity.REQUEST_CODE:
                    UserBean user = (UserBean) data.getSerializableExtra("user");
                    if (user != null) {
                        updateHeaderLayout(user);
                    }
                    // TODO: 2017/3/16 这里需要绑定对应的用户
                    presenter.fetchListData(msgID, listData.size());
                    break;
                case RESULT_OK:
                    //语音识别成功后获取语音的文本形式内容的回调接口
                    break;
            }
        }
    }

    private void updateHeaderLayout(UserBean user) {
        if (!Global.isLogin){
            loggedInLayout.setVisibility(View.GONE);
            withoutLoginLayout.setVisibility(View.VISIBLE);
        } else {
            withoutLoginLayout.setVisibility(View.GONE);
            loggedInLayout.setVisibility(View.VISIBLE);
            if (user != null){
                //TODO:暂未获取成长值和天气预报
                String name = user.getUsername();
                String growth = user.getGrowthValue();
                String imgPath = null;
                if (name != null){
                    userName.setText(name);
                }
//                Bitmap bitmap = PhotoUtils.getSpecifiedBitmap(imgPath, 100, 100);
//                if (bitmap != null){
//                    userFace.setImageBitmap(bitmap);
//                } else {
//                    userFace.setImageDrawable(getResources().getDrawable(R.drawable.default_user_image));
//                }
                if (growth != null){
                    growthValue.setText(growth);
                }
            }
            //todo 更改聊天界面用户头像
        }
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
                } else {
                    initListViewData(null);
                }
            }
        };
        presenter.loadLoginCache(subscriber);
    }

    @Override
    public void initListViewData(List<MessageBean> messageList){
        listData.clear();
        if (Global.isLogin){
            if (messageList != null && messageList.size() > 0){
                for (MessageBean bean : messageList) {
//                setHandleMsgBean(item, 0);
                    listData.add(0, bean);
                }
            } else {
                MessageBean messageBean =new MessageBean(getString(R.string.welcome_tip), false,
                        System.currentTimeMillis());
                listData.add(messageBean);
                presenter.save(messageBean);
            }
        } else{
            listView.setPullRefreshEnable(false);
            MessageBean messageBean =new MessageBean(getString(R.string.welcome_tip), false,
                    System.currentTimeMillis());
            listData.add(messageBean);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (System.currentTimeMillis() - exitTime > 2000) {
            ToastUtil.shortShow(getString(R.string.exit_tip));
            exitTime = System.currentTimeMillis();
        } else {
//            finish();
//            System.exit(0);
            super.onBackPressed();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.setVoiceLanguage:
                setVoiceLanguage();
                break;
            case R.id.recognizeWakeUp:
//                setASRWakeUp();
                break;
            case R.id.helpBayMin:
                AdviseQAActivity.start(this);
                break;
            case R.id.setBackground:
                ToastUtil.shortShow("待完善");
                break;
            case R.id.introduction:
                IntroductionActivity.start(this);
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
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
                String tips = getResources().getString(R.string.currentLanguage) + languageTypes[which];
                ToastUtil.show(tips);
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
            case R.id.inputType:
                changeInputType();
                break;
            case R.id.voiceButton:
                startSpeechReconDialog();
                break;
            case R.id.sendButton:
                handleSendQuestion();
                break;
            case R.id.speak_finish:
                //todo 识别语音
                ToastUtil.shortShow("识别完成");
                dialog.dismiss();
                break;
            case R.id.header_to_login:
                LoginActivity.startForResult(MainActivity.this);
                break;
            case R.id.face:
                PersonalInfoActivity.startForResult(MainActivity.this);
            case R.id.temperature:
            case R.id.place:
                WeatherActivity.startForResult(MainActivity.this);
                break;
            default:
                break;
        }
    }

    /**
     * 更改底部输入面板视图类型
     */
    private void changeInputType() {
        if (voiceButton.isShown()) {
            inputType.setImageResource(R.drawable.voice);
            voiceButton.setVisibility(View.GONE);
            editText.setVisibility(View.VISIBLE);
            sendButton.setVisibility(View.VISIBLE);
            inputMethodManager.showSoftInputFromInputMethod(editText.getWindowToken(), 0);
        } else {
            inputType.setImageResource(R.drawable.edit);
            editText.setVisibility(View.GONE);
            sendButton.setVisibility(View.GONE);
            voiceButton.setVisibility(View.VISIBLE);
            inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }

    @SuppressWarnings("unchecked")
    private void handleSendQuestion() {
        sendButton.setBackgroundResource(R.drawable.bg_button);
        String question = editText.getText().toString().trim();
        if (TextUtils.isEmpty(question)){
            ToastUtil.shortShow(getString(R.string.null_tips));
        } else if (getString(R.string.close_asr).equals(question)) {
            //todo 关闭语音连续识别
        } else {
            final MessageBean sendMsg = new MessageBean(question, MessageBean.TYPE_SEND, System.currentTimeMillis());
            listData.add(sendMsg);
            adapter.notifyDataSetChanged();
            Subscriber subscriber = new Subscriber<MessageBean>() {
                @Override
                public void onCompleted() {
                }

                @Override
                public void onError(Throwable e) {
//                    listData.get(0).isSendSuccessful = false;
//                    listData.get(0).isSending = false;
                    LogUtil.d("onError: " + e.getMessage());
                    sendMsg.isSending = false;
                    sendMsg.isSendSuccessful = false;
                    listData.remove(listData.size() - 1);
                    listData.add(sendMsg);
                    adapter.notifyDataSetChanged();
                    //todo 小白回复：网络错误，请重试
                    presenter.save(sendMsg);
                }

                @Override
                public void onNext(MessageBean messageBean) {
                    LogUtil.d("onNext");
                    sendMsg.isSending = false;
                    sendMsg.isSendSuccessful = true;
                    listData.remove(listData.size() - 1);
                    listData.add(sendMsg);
                    presenter.save(sendMsg);

                    listData.add(messageBean);
                    presenter.save(messageBean);
                    //todo 语音合成答案
                    adapter.notifyDataSetChanged();
                }
            };
            presenter.getAnswer(question, subscriber);
            editText.setText("");
        }
    }

    private void startSpeechReconDialog() {
        if (dialog == null){
            dialog = new SpeechRecognizeDialog(this, R.style.Theme_RecognitionDialog);
            dialog.setOnClickListener(this);
        }
        dialog.show();
        dialog.setText(R.string.speech_preper);
        dialog.setImageResource(R.drawable.v1);
    }

    @Override
    public void onRefresh() {
        if (!mPullRefreshing){
            mPullRefreshing = true;
            Subscriber<List<MessageBean>> subscriber = new Subscriber<List<MessageBean>>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(List<MessageBean> beans) {
                    if (beans != null){
                        for (MessageBean bean : beans){
                            listData.add(0, bean);
                        }
                    }
                    if (beans == null || beans.size() < 20){
                        listView.setPullRefreshEnable(false);
                    }
                    listView.stopRefresh();
                    mPullRefreshing = false;
                    adapter.notifyDataSetChanged();
                    listView.setSelection(0);
                }
            };
            presenter.onRefreshing(msgID, listData.size(), subscriber);
        }
    }

    @Override
    public void onLoadMore() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
        presenter.onDestroy();
    }
}
