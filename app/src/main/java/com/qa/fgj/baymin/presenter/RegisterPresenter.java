package com.qa.fgj.baymin.presenter;

import android.content.Context;

import com.qa.fgj.baymin.R;
import com.qa.fgj.baymin.base.IBasePresenter;
import com.qa.fgj.baymin.model.RegisterModel;
import com.qa.fgj.baymin.model.entity.UserBean;
import com.qa.fgj.baymin.ui.view.IRegisterView;
import com.qa.fgj.baymin.util.Global;
import com.qa.fgj.baymin.util.MD5Util;
import com.qa.fgj.baymin.util.SystemUtil;

import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;

/**
 * Created by FangGengjia on 2017/2/18.
 */

public class RegisterPresenter<T extends IRegisterView> implements IBasePresenter<T> {

    private Context mContext;
    private RegisterModel mModel;
    private IRegisterView mView;
    private Scheduler executor;
    private Scheduler notifier;

    private String mName;
    private String mEmail;
    private String mPassword;
    private String mConfirmPassword;

    private Subscription subscription;

    public RegisterPresenter(Context context, Scheduler notifier, Scheduler executor) {
        mContext = context;
        this.notifier = notifier;
        this.executor = executor;
    }

    @Override
    public void attachView(IRegisterView view) {
        this.mView = view;
    }

    public void inputName(String name) {
        this.mName = name;
    }

    public void inputEmail(String email) {
        this.mEmail = email;
    }

    public void inputPassword(String password) {
        this.mPassword = password;
    }

    public void inputConfirmPassword(String confirmPassword) {
        this.mConfirmPassword = confirmPassword;
    }

    public void signUp(Subscriber<UserBean> subscriber){
        if (!SystemUtil.isNetworkConnected()){
            mView.onSignUpFailed("网络不可用，请检查你的网络");
            return;
        }

        if (!validate()){
            mView.onSignUpFailed(null);
            return;
        }

        mView.showProgressDialog();

        subscription = mModel.signUp(generateUserBean())
                .observeOn(notifier)
                .subscribeOn(executor)
                .subscribe(subscriber);
    }

    private boolean validate() {
        boolean valid = true;

        if (mName.isEmpty() || mName.length() < 3) {
            mView.inputNameError(mContext.getString(R.string.username_wrong_length));
            valid = false;
        } else {
            mView.inputNameError(null);
        }

        if (mEmail.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(mEmail).matches()) {
            mView.inputEmailError(mContext.getString(R.string.wrong_email));
            valid = false;
        } else {
            mView.inputEmailError(null);
        }

        if (mPassword.isEmpty() || mPassword.length() < 6 || mPassword.length() > 20) {
            mView.inputPasswordError(mContext.getString(R.string.psw_wrong_length));
            valid = false;
        } else if (!mPassword.equals(mConfirmPassword)){
            mView.inputConfirmPasswordError(mContext.getString(R.string.wrong_to_confirm_psw));
            valid = false;
        } else{
            mView.inputPasswordError(null);
            mView.inputConfirmPasswordError(null);
        }
        return valid;
    }

    private UserBean generateUserBean(){
        UserBean userBean = new UserBean();
        userBean.setUsername(mName);
        userBean.setEmail(mEmail);
        userBean.setPassword(MD5Util.getMD5Digest(mPassword));
        userBean.setImagePath("");
        userBean.setGrowthValue("0");
        return userBean;
    }

    public void save(UserBean userBean){
        if (Global.userInfoDB == null){
            Global.initDB();
        }
        Global.userInfoDB.save(userBean);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void detachView() {
        mView = null;
    }

    @Override
    public void onDestroy() {
        if (subscription != null && !subscription.isUnsubscribed()){
            subscription.unsubscribe();
        }
    }
}
