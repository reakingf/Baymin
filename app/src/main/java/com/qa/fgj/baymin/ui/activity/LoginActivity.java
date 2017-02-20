package com.qa.fgj.baymin.ui.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.qa.fgj.baymin.R;
import com.qa.fgj.baymin.base.BaseActivity;
import com.qa.fgj.baymin.model.entity.UserBean;
import com.qa.fgj.baymin.presenter.LoginPresenter;

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
    LoginPresenter mPresenter;
    Scheduler executor = AndroidSchedulers.mainThread();
    Scheduler notifier = Schedulers.newThread();
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
        mPresenter = new LoginPresenter(this, notifier, executor);
        mPresenter.fetchCache();

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

        mPresenter.inputEmail(emailText.getText().toString().trim());
        mPresenter.inputPassword(passwordText.getText().toString().trim());
        mPresenter.isSelectRememberBox(isRememberBox.isChecked());

        Subscriber<UserBean> subscriber = new Subscriber<UserBean>() {
            @Override
            public void onCompleted() {
                dismissProgressDialog();
            }

            @Override
            public void onError(Throwable e) {
                onLoginFailed("网络连接超时，请重试");
            }

            @Override
            public void onNext(UserBean userBean) {
                switch (userBean.getUsername()) {
                    case "邮箱未注册":
                        onLoginFailed(getString(R.string.has_not_sign_up));
                        break;
                    case "密码错误":
                        onLoginFailed(getString(R.string.wrong_psw));
                        break;
                    default:
                        onLoginSuccess(userBean);
                        break;
                }
            }
        };

        mPresenter.login(subscriber);
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
                String password = user.getPassword();
                emailText.setText(user.getEmail());
                passwordText.setText(password);
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
        loginButton.setEnabled(true);
        if (user != null){
            mPresenter.onLoginSuccess(user);
        }
    }

    public void onLoginFailed(String tips) {
        if (tips == null)
            Toast.makeText(getBaseContext(), getString(R.string.login_failed), Toast.LENGTH_LONG).show();
        else
            Toast.makeText(getBaseContext(), tips, Toast.LENGTH_LONG).show();
        loginButton.setEnabled(true);
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
        data.putExtra("user",user);
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
    }
}