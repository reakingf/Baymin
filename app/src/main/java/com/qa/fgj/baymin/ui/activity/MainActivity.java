package com.qa.fgj.baymin.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
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
import com.qa.fgj.baymin.app.Constant;
import com.qa.fgj.baymin.base.BaseActivity;
import com.qa.fgj.baymin.model.entity.BayMinResponse;
import com.qa.fgj.baymin.model.entity.MessageBean;
import com.qa.fgj.baymin.model.entity.UserBean;
import com.qa.fgj.baymin.presenter.MainPresenter;
import com.qa.fgj.baymin.ui.service.AppUpdateManager;
import com.qa.fgj.baymin.ui.view.IMainView;
import com.qa.fgj.baymin.ui.adapter.MsgAdapter;
import com.qa.fgj.baymin.util.Global;
import com.qa.fgj.baymin.util.PhotoUtils;
import com.qa.fgj.baymin.util.ToastUtil;
import com.qa.fgj.baymin.widget.RoundImageView;
import com.qa.fgj.baymin.widget.SelectableDialog;
import com.qa.fgj.baymin.widget.ShowTipDialog;
import com.qa.fgj.baymin.widget.SpeechRecognizeDialog;
import com.qa.fgj.baymin.widget.XListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

    private SelectableDialog chooseLanguageDialog;
    private ShowTipDialog continuousRecogDialog;
    private SelectableDialog setLanguageDialog;
    private ShowTipDialog exitDialog;

    //语音识别对话框
    private SpeechRecognizeDialog asrDialog;

    UserBean mUserBean = null;
    private List<MessageBean> listData = new ArrayList<>();
    private MsgAdapter adapter;
    //初始消息id
    private String msgID = "0";
    private boolean mPullRefreshing = false;

    private InputMethodManager inputMethodManager;
    // 用于设置系统语言
    Configuration configuration;
    //用于保存系统语言类型
    SharedPreferences setConfig;
    //用于修改系统语言类型
    SharedPreferences.Editor setEditor;
    //存储当前系统语言
    String currentLanguage = "";
    private long exitTime = 0;
    private AppUpdateManager updateManager;

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
        recoveryLanguage();
        setContentView(R.layout.activity_main);
        initView();
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(new MobilePhoneStateListener(), PhoneStateListener.LISTEN_CALL_STATE);
        presenter = new MainPresenter(this, uiThread, backgroundThread);
        presenter.onCreate();
        presenter.attachView(this);
        checkShouldLogin();
        asrDialog = new SpeechRecognizeDialog(this, R.style.Theme_RecognitionDialog);
        asrDialog.setOnClickListener(this);
        presenter.startWakeUp();
    }

    private void initView() {
        setSupportActionBar(toolbar);
        initDrawerLayout();
        inputLayout = (LinearLayout) findViewById(R.id.msgButtonBar);
        inputType = (ImageButton) findViewById(R.id.inputType);
        voiceButton = (Button) findViewById(R.id.voiceButton);
        editText = (EditText) findViewById(R.id.textInput);
        sendButton = (Button) findViewById(R.id.sendButton);
        listView = (XListView) findViewById(R.id.listContent);
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
        listView.setPullLoadEnable(false);
        adapter = new MsgAdapter(this, R.layout.item_chat_list, listData, mUserBean);
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
                    UserBean loginUser = (UserBean) data.getSerializableExtra("user");
                    if (loginUser != null) {
                        updateHeaderLayout(loginUser);
                    }
                    // TODO: 2017/3/16 这里需要绑定对应的用户
                    presenter.fetchListData(msgID, listData.size());
                    break;
                case PersonalInfoActivity.REQUEST_CODE:
                    if (!Global.isLogin){
                        loggedInLayout.setVisibility(View.GONE);
                        withoutLoginLayout.setVisibility(View.VISIBLE);
                        initListViewData(null);
                    } else {
                        UserBean changeUser = (UserBean) data.getSerializableExtra("user");
                        if (changeUser != null)
                            updateHeaderLayout(changeUser);
                    }
                    break;
                case RESULT_OK:
                    //语音识别成功后获取语音的文本形式内容的回调接口
                    asrDialog.onActivityResult(data);
                    break;
            }
        }
    }

    private void  updateHeaderLayout(UserBean user) {
        if (!Global.isLogin){
            loggedInLayout.setVisibility(View.GONE);
            withoutLoginLayout.setVisibility(View.VISIBLE);
        } else {
            withoutLoginLayout.setVisibility(View.GONE);
            loggedInLayout.setVisibility(View.VISIBLE);
            if (user != null){
                //TODO:暂未获取成长值和天气预报
                mUserBean = user;
                String name = user.getUsername();
                String growth = user.getGrowthValue();
                String imgPath = user.getImagePath();
                if (name != null){
                    userName.setText(name);
                }
                Bitmap bitmap = new PhotoUtils(this).getSpecifiedBitmap(imgPath, 100, 100);
                if (bitmap != null){
                    userFace.setImageBitmap(bitmap);
                } else {
                    userFace.setImageDrawable(getResources().getDrawable(R.drawable.default_user_image));
                }
                if (growth != null){
                    growthValue.setText(growth);
                }
            }
            initListView();
            listView.setSelection(listView.getBottom());
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
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.setVoiceLanguage:
                setVoiceLanguage();
                break;
            case R.id.continuousRecognition:
                setContinuousRecognition();
                break;
            case R.id.helpBayMin:
                AdviseQAActivity.start(this);
                break;
            case R.id.setBackground:
                // TODO: 主题更换
                ToastUtil.shortShow("待完善");
                break;
            case R.id.introduction:
                IntroductionActivity.start(this);
                break;
            case R.id.checkVersion:
                updateManager = new AppUpdateManager(MainActivity.this);
                updateManager.onCreate();
                break;
            case R.id.setLanguage:
                showLangSettingDialog();
                break;
            case R.id.exit:
                setExitApp();
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    @Override
    public void startRecognition(){
        if (asrDialog == null){
            asrDialog = new SpeechRecognizeDialog(this, R.style.Theme_RecognitionDialog);
            asrDialog.setOnClickListener(this);
        }
        asrDialog.setText(R.string.speech_preper);
        asrDialog.setImageResource(R.drawable.v1);
        asrDialog.show();
    }

    @Override
    public boolean isRecognizing() {
        return asrDialog != null && asrDialog.isShowing();
    }

    @Override
    public void musicStateTip(String tip) {
        MessageBean resp_msg3 = new MessageBean(tip, MessageBean.TYPE_RECEIVED, System.currentTimeMillis());
        listData.add(resp_msg3);
        adapter.notifyDataSetChanged();
        presenter.speak(tip);
        presenter.save(resp_msg3);
    }

    /**
     * 设置语音输入类型
     */
    public void setVoiceLanguage() {
        chooseLanguageDialog = new SelectableDialog(this);
        chooseLanguageDialog.setTitleText(getString(R.string.setVoiceLanguageTitle));
        chooseLanguageDialog.setFirstItem("普通话(中国)", new SelectableDialog.ItemClickListener() {
            @Override
            public void onClick() {
                asrDialog.setCurrentLanguage(0);
                String tips = getResources().getString(R.string.currentLanguage) + "普通话(中国)";
                ToastUtil.show(tips);
                chooseLanguageDialog.dismiss();
            }
        });
        chooseLanguageDialog.setSecondItem("English(United State)", new SelectableDialog.ItemClickListener() {
            @Override
            public void onClick() {
                asrDialog.setCurrentLanguage(1);
                String tips = getResources().getString(R.string.currentLanguage) + "English(United State)";
                ToastUtil.show(tips);
                chooseLanguageDialog.dismiss();
            }
        });
        chooseLanguageDialog.show();
    }

    private void setContinuousRecognition(){
        continuousRecogDialog = new ShowTipDialog(this);
        continuousRecogDialog.setTitleText(getString(R.string.continuousRecognition));
        continuousRecogDialog.setContentText(getString(R.string.isContinueRecognition));
        continuousRecogDialog.setPositiveButton(getString(R.string.startup),
                new ShowTipDialog.onPositiveButtonClick() {
                    @Override
                    public void onClick() {
                        continuousRecogDialog.dismiss();
                        presenter.setContinueASRStart(true);
                        presenter.setShouldStartRecognition(true);
                        presenter.startContinuousASR();
                    }
                });
        continuousRecogDialog.setNegativeButton(getString(R.string.close),
                new ShowTipDialog.onNegativeButtonClick() {
                    @Override
                    public void onClick() {
                        continuousRecogDialog.dismiss();
                        presenter.stopContinuousASR();
                    }
                });
        continuousRecogDialog.show();
    }

    @Override
    public void showError(String msg) {
        ToastUtil.shortShow(msg);
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
                presenter.startRecognition();
                break;
            case R.id.sendButton:
                presenter.stopSpeak();
                handleSendQuestion(editText.getText().toString().trim());
                break;
            case R.id.speak_finish:
                asrDialog.stopListening();
                break;
            case R.id.header_to_login:
                LoginActivity.startForResult(MainActivity.this);
                break;
            case R.id.face:
                startPersonalActivity();
                break;
            case R.id.temperature:
            case R.id.place:
                WeatherActivity.startForResult(MainActivity.this);
                break;
            default:
                break;
        }
    }

    //提供给adapter调用
    public void startPersonalActivity(){
        PersonalInfoActivity.startForResult(MainActivity.this, userName.getText().toString());
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
    public void handleSendQuestion(String question) {
        sendButton.setBackgroundResource(R.drawable.bg_button);
        if (TextUtils.isEmpty(question)){
            ToastUtil.shortShow(getString(R.string.null_tips));
        } else if (getString(R.string.close_asr).equals(question)) {
            presenter.stopContinuousASR();
        } else {
            presenter.setShouldStartRecognition(false);
            final MessageBean sendMsg = new MessageBean(question, MessageBean.TYPE_SEND, System.currentTimeMillis());
            listData.add(sendMsg);
            adapter.notifyDataSetChanged();
            Subscriber<BayMinResponse<String>> subscriber = new Subscriber<BayMinResponse<String>>() {
                @Override
                public void onCompleted() {
                }

                @Override
                public void onError(Throwable e) {
//                    listData.get(0).isSendSuccessful = false;
//                    listData.get(0).isSending = false;
                    ToastUtil.shortShow(e.getMessage() == null ? "发送失败" : e.getMessage());
                    sendMsg.isSending = false;
                    sendMsg.isSendSuccessful = false;
                    listData.remove(listData.size() - 1);
                    listData.add(sendMsg);
                    adapter.notifyDataSetChanged();
                    //todo 小白回复：网络错误，请重试
                    presenter.save(sendMsg);
                    presenter.setShouldStartRecognition(true);
                }

                @Override
                public void onNext(BayMinResponse<String> response) {
                    if (!response.isSucceed()){
                        ToastUtil.shortShow(response.getMessage() == null ? "发送失败" : response.getMessage());
                    } else {
                        sendMsg.isSending = false;
                        sendMsg.isSendSuccessful = true;
                        listData.remove(listData.size() - 1);
                        listData.add(sendMsg);
                        presenter.save(sendMsg);

                        MessageBean messageBean = new MessageBean();
                        messageBean.isSending = false;
                        messageBean.isSendSuccessful = true;
                        messageBean.isSendMsg = false;
                        String answer = response.getContent();
                        answer = presenter.checkCommand(answer);
                        messageBean.setContent(answer);
                        messageBean.setCreateTime(System.currentTimeMillis());
                        listData.add(messageBean);
                        presenter.save(messageBean);
                        presenter.speak(answer);
                        adapter.notifyDataSetChanged();
                    }
                }
            };
            presenter.getAnswer(question, subscriber);
        }
        editText.setText("");
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
                    //todo 调用stopRefresh会出错，停止后无法定位到第一条消息
//                    listView.stopRefresh();
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

    /**
     * 设置系统语言
     */
    public void showLangSettingDialog() {
        setLanguageDialog = new SelectableDialog(this);
        setLanguageDialog.setTitleText(getString(R.string.setLanguage));
        setLanguageDialog.setFirstItem("中文(简体)", new SelectableDialog.ItemClickListener() {
            @Override
            public void onClick() {
                setLanguageDialog.dismiss();
                setSystemLanguage(0);
            }
        });
        setLanguageDialog.setSecondItem("English", new SelectableDialog.ItemClickListener() {
            @Override
            public void onClick() {
                setLanguageDialog.dismiss();
                setSystemLanguage(1);
            }
        });
        setLanguageDialog.show();
    }

    private void setSystemLanguage(int index){
        final String[] languages = {"中文(简体)", "English"};
        setEditor = setConfig.edit();
        if (index == 0){
            configuration.locale = Locale.CHINA;
            currentLanguage = "CHINA";
            setEditor.putString("language", currentLanguage);
        }else {
            configuration.locale = Locale.ENGLISH;
            currentLanguage = "ENGLISH";
            setEditor.putString("language", currentLanguage);
        }
        getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());
        boolean ok = setEditor.commit();
        if (ok){
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }else {
            String tip = getResources().getString(R.string.changeFail) + languages[index];
            ToastUtil.shortShow(tip);
        }
    }

    /**
     * 恢复到上一次设置好的语言，若没有即为手机系统当前语言
     */
    private void recoveryLanguage(){
        configuration = getResources().getConfiguration();
        setConfig = getSharedPreferences("set", MODE_PRIVATE);
        String lang = setConfig.getString("language", null);
        if (lang != null){
            if (lang.equals("CHINA")){
                configuration.locale = Locale.CHINA;
            }
            else{
                configuration.locale = Locale.ENGLISH;
            }
            getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());
        }
    }
    /**
     * 关闭应用程序
     */
    public void setExitApp() {
        exitDialog = new ShowTipDialog(this);
        exitDialog.setTitleText("提示信息");
        exitDialog.setContentText(getString(R.string.exit_hint));
        exitDialog.setPositiveButton(getString(R.string.ok), new ShowTipDialog.onPositiveButtonClick() {
            @Override
            public void onClick() {
                exitDialog.dismiss();
                System.exit(0);
            }
        });
        exitDialog.setNegativeButton(getString(R.string.cancel), new ShowTipDialog.onNegativeButtonClick() {
            @Override
            public void onClick() {
                exitDialog.dismiss();
            }
        });
        exitDialog.show();
    }

    /**
     * 通话监听器类
     */
    private class MobilePhoneStateListener extends PhoneStateListener {

        //标志是否第一次进入该App，若是则不执行挂机状态事件处理
        private boolean isFirst = true;

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE: // 挂机状态
                    Intent idleIntent = new Intent();
                    idleIntent.setAction(Constant.PHONE_STATE_ACTION);
                    idleIntent.putExtra("MUSIC_MSG", Constant.MUSIC_CONTINUE);
                    idleIntent.putExtra("isFirst", isFirst);
                    sendBroadcast(idleIntent);
                    isFirst = false;
                    presenter.setShouldStartRecognition(true);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:    //通话状态
                    break;
                case TelephonyManager.CALL_STATE_RINGING:    //响铃状态
                    Intent ringingIntent = new Intent();
                    ringingIntent.setAction(Constant.PHONE_STATE_ACTION);
                    ringingIntent.putExtra("MUSIC_MSG", Constant.MUSIC_PAUSE);
                    ringingIntent.putExtra("TTS_MSG", Constant.TTS_STOP);
                    sendBroadcast(ringingIntent);
                    presenter.setShouldStartRecognition(false);
                    if (asrDialog != null && asrDialog.isShowing()) {
                        asrDialog.dismiss();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void destroyDialog(Dialog dialog){
        if (dialog != null && dialog.isShowing()){
            dialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyDialog(chooseLanguageDialog);
        destroyDialog(continuousRecogDialog);
        destroyDialog(setLanguageDialog);
        destroyDialog(exitDialog);
        if (asrDialog != null){
            asrDialog.onDestroy();
        }
        if (updateManager != null){
            updateManager.onDestroy();
        }
        presenter.detachView();
        presenter.onDestroy();
    }
}
