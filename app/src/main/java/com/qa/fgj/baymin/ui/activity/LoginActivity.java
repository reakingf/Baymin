package com.qa.fgj.baymin.ui.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.qa.fgj.baymin.R;
import com.qa.fgj.baymin.base.BaseActivity;
import com.qa.fgj.baymin.model.entity.BayMinResponse;
import com.qa.fgj.baymin.model.entity.UserBean;
import com.qa.fgj.baymin.presenter.LoginPresenter;
import com.qa.fgj.baymin.ui.view.ILoginView;
import com.qa.fgj.baymin.util.LogUtil;
import com.qa.fgj.baymin.util.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by FangGengjia on 2017/2/19
 * 用户登录类
 */

public class LoginActivity extends BaseActivity implements ILoginView {

    @BindView(R.id.input_email)
    EditText emailText;
    @BindView(R.id.input_password)
    EditText passwordText;
    @BindView(R.id.isRemember)
    CheckBox isRememberBox;
    @BindView(R.id.btn_login)
    Button loginButton;
    @BindView(R.id.link_register)
    TextView registerLink;

    ProgressDialog mProgressDialog;
    LoginPresenter presenter;
    Scheduler uiThread = AndroidSchedulers.mainThread();
    Scheduler backgroundThread = Schedulers.newThread();
    public static final int REQUEST_CODE = 0xaa01;

    public static void start(Context context){
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    public static void startForResult(Activity caller){
        Intent intent = new Intent(caller, LoginActivity.class);
        caller.startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mProgressDialog = new ProgressDialog(LoginActivity.this, R.style.AppTheme_Dark_Dialog);
        presenter = new LoginPresenter(this, uiThread, backgroundThread);
        presenter.onCreate();
        presenter.attachView(this);
        presenter.fetchCache();

        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        registerLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                RegisterActivity.startForResult(LoginActivity.this);
            }
        });
    }

    @Override
    public void bindData(String email, String password, boolean isRemember) {
        emailText.setText(email);
        if (isRemember){
            passwordText.setText(password);
            isRememberBox.setChecked(true);
            login();
        }
    }

    public void login() {
        loginButton.setEnabled(false);

        presenter.inputEmail(emailText.getText().toString().trim());
        presenter.inputPassword(passwordText.getText().toString().trim());
        presenter.isSelectRememberBox(isRememberBox.isChecked());

        Subscriber<BayMinResponse<UserBean>> subscriber = new Subscriber<BayMinResponse<UserBean>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                onLoginFailed(TextUtils.isEmpty(e.getMessage()) ? "网络连接超时，请重试" : e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onNext(BayMinResponse<UserBean> response) {
//                switch (userBean.getUsername()) {
//                    case "邮箱未注册":
//                        onLoginFailed(getString(R.string.has_not_sign_up));
//                        break;
//                    case "密码错误":
//                        onLoginFailed(getString(R.string.wrong_psw));
//                        break;
//                    default:
//                        onLoginSuccess(userBean);
//                        break;
//                }
                if (!response.isSucceed()){
                    onLoginFailed(response.getMessage());
                } else if (response.getContent() == null){
                    onLoginFailed("数据解析错误");
                } else {
                    onLoginSuccess(response.getContent());
                    presenter.saveLoginCache();
                }
            }
        };

        presenter.login(subscriber);
    }

    @Override
    public void inputEmailError(String msg){
        emailText.setError(msg);
    }

    @Override
    public void inputPasswordError(String msg){
        passwordText.setError(msg);
    }

    @Override
    public void showProgressDialog(){
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getString(R.string.login_validating));
        mProgressDialog.show();
    }

    @Override
    public void dismissProgressDialog(){
        mProgressDialog.dismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RegisterActivity.REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                UserBean user = (UserBean) data.getSerializableExtra("user");
                emailText.setText(user.getEmail());
                passwordText.setText(user.getPassword());
                login();
            }
        }
    }

//    @Override
//    public void onBackPressed() {
//        // 不改变栈中任务顺序，将该Activity至于后台，不finish，不返回到MainActivity
//        moveTaskToBack(true);
//    }

    public void onLoginSuccess(final UserBean user) {
        mProgressDialog.dismiss();
        loginButton.setEnabled(true);
        if (user != null){
            presenter.onLoginSuccess(user);
        }
    }

    public void onLoginFailed(String tips) {
        mProgressDialog.dismiss();
        loginButton.setEnabled(true);
        if (tips == null)
            ToastUtil.show(getString(R.string.login_failed));
        else
            ToastUtil.show(tips);
    }

    @Override
    public void showError(String msg) {

    }

    @Override
    public void useNightMode(boolean isNight) {

    }

    @Override
    public void finishWithResult(UserBean user){
        Intent data = new Intent();
        data.putExtra("user", user);
        setResult(RESULT_OK, data);
        finish();
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
