package com.qa.fgj.baymin.ui.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.qa.fgj.baymin.R;
import com.qa.fgj.baymin.base.BaseActivity;
import com.qa.fgj.baymin.model.entity.BayMinResponse;
import com.qa.fgj.baymin.model.entity.UserBean;
import com.qa.fgj.baymin.presenter.RegisterPresenter;
import com.qa.fgj.baymin.ui.view.IRegisterView;
import com.qa.fgj.baymin.util.Global;
import com.qa.fgj.baymin.util.LogUtil;
import com.qa.fgj.baymin.util.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by FangGengjia on 2017/2/18
 * 用户注册类
 */

public class RegisterActivity extends BaseActivity implements IRegisterView {

    private static final String TAG = RegisterActivity.class.getSimpleName();

    @BindView(R.id.input_name)
    EditText nameText;
    @BindView(R.id.input_email)
    EditText emailText;
    @BindView(R.id.input_password)
    EditText passwordText;
    @BindView(R.id.input_confirm_password)
    EditText confirmPasswordText;
    @BindView(R.id.btn_register)
    Button registerButton;
    @BindView(R.id.link_login)
    TextView loginLink;

    ProgressDialog mProgressDialog;
    RegisterPresenter presenter;
    Scheduler uiThread = AndroidSchedulers.mainThread();
    Scheduler backgroundThread = Schedulers.newThread();

    public static final int REQUEST_CODE = 0xac01;

    public static void start(final Context context){
        Intent intent = new Intent(context, RegisterActivity.class);
        context.startActivity(intent);
    }

    public static void startForResult(final Activity caller){
        Intent intent = new Intent(caller, RegisterActivity.class);
        caller.startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
//        Global.initDB();
        ButterKnife.bind(this);
        mProgressDialog = new ProgressDialog(RegisterActivity.this, R.style.AppTheme_Dark_Dialog);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });
        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        presenter = new RegisterPresenter(this, backgroundThread, uiThread);
        presenter.attachView(this);
        presenter.onCreate();
    }

    public void signUp() {
        //防止用户重复点击
        registerButton.setEnabled(false);

        presenter.inputName(nameText.getText().toString().trim());
        presenter.inputEmail(emailText.getText().toString().trim());
        presenter.inputPassword(passwordText.getText().toString().trim());
        presenter.inputConfirmPassword(confirmPasswordText.getText().toString().trim());

        Subscriber<BayMinResponse<UserBean>> subscriber = new Subscriber<BayMinResponse<UserBean>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                onSignUpFailed(TextUtils.isEmpty(e.getMessage()) ? "网络连接超时，请重试" : e.getMessage());
            }

            @Override
            public void onNext(BayMinResponse<UserBean> response) {
                if (!response.isSucceed()){
                    onSignUpFailed(response.getMessage());
                } else if (response.getContent() == null){
                    onSignUpFailed("数据解析错误");
                } else {
                    onSignUpSuccess(response.getContent());
                }
            }
        };

        presenter.signUp(subscriber);
    }

    @Override
    public void showProgressDialog(){
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getString(R.string.registering));
        mProgressDialog.show();
    }

    @Override
    public void dismissProgressDialog(){
        mProgressDialog.dismiss();
    }

    @Override
    public void onSignUpSuccess(UserBean userBean) {
        registerButton.setEnabled(true);
        presenter.save(userBean);
        Intent intent = new Intent();
        //将明文密码传给登录界面
        userBean.setPassword(passwordText.getText().toString().trim());
        intent.putExtra("user", userBean);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onSignUpFailed(String tips) {
        registerButton.setEnabled(true);
        if (tips == null)
            ToastUtil.show(getString(R.string.register_failed));
        else
            ToastUtil.show(tips);
    }

    public void inputNameError(String msg){
        nameText.setError(msg);
    }

    public void inputEmailError(String msg){
        emailText.setError(msg);
    }

    public void inputPasswordError(String msg){
        passwordText.setError(msg);
    }

    public void inputConfirmPasswordError(String msg){
        confirmPasswordText.setError(msg);
    }

    @Override
    public void useNightMode(boolean isNight) {

    }

    @Override
    public void showError(String msg) {

    }

    @Override
    protected void onDestroy() {
//        Global.closeDB();
        super.onDestroy();
        if (mProgressDialog != null){
            if (mProgressDialog.isShowing()){
                mProgressDialog.dismiss();
            }
            mProgressDialog = null;
        }
        presenter.detachView();
        presenter.onDestroy();
    }
}