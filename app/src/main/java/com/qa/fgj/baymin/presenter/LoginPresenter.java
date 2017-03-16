package com.qa.fgj.baymin.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.qa.fgj.baymin.R;
import com.qa.fgj.baymin.base.IBasePresenter;
import com.qa.fgj.baymin.model.LoginModel;
import com.qa.fgj.baymin.model.entity.UserBean;
import com.qa.fgj.baymin.ui.view.ILoginView;
import com.qa.fgj.baymin.util.Global;
import com.qa.fgj.baymin.util.LogUtil;
import com.qa.fgj.baymin.util.MD5Util;
import com.qa.fgj.baymin.util.SystemUtil;

import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by FangGengjia on 2017/2/18.
 */

public class LoginPresenter<T extends ILoginView> implements IBasePresenter<T> {

    private Context mContext;
    private T mView;
    private LoginModel mModel;
    private Scheduler mExecutor;
    private Scheduler mNotifier;

    private SharedPreferences mSP;
    private SharedPreferences.Editor mEditor;
    private String mEmail;
    private String mPassword;
    private boolean mIsRemember;
    private Subscription subscription;

    public LoginPresenter(Context context, Scheduler notifier, Scheduler executor) {
        this.mContext = context;
        mNotifier = notifier;
        mExecutor = executor;
    }

    @Override
    public void onCreate() {
        mModel = new LoginModel();
    }

    @Override
    public void attachView(T view) {
        mView = view;
    }

    public void fetchCache(){
        mSP = mContext.getSharedPreferences("loginInfo", MODE_PRIVATE);
        mView.bindData(mSP.getString("email",""), mSP.getString("password",""),
                mSP.getBoolean("isRemember", false));
    }

    public void inputEmail(String email){
       mEmail = email;
    }

    public void inputPassword(String password){
        mPassword = password;
    }

    public void isSelectRememberBox(boolean isRemember){
        mIsRemember = isRemember;
    }

    public void login(Subscriber<UserBean> subscriber){

        if (!SystemUtil.isNetworkConnected()){
            mView.onLoginFailed("网络不可用，请检查网络");
            return;
        }

        if (!validate()) {
            mView.onLoginFailed(null);
            return;
        }

        UserBean userBean = new UserBean();
        userBean.setEmail(mEmail);
        userBean.setPassword(MD5Util.getMD5Digest(mPassword));

        mView.showProgressDialog();
        subscription = mModel.login(userBean)
                .subscribeOn(mNotifier)
                .observeOn(mExecutor)
                .subscribe(subscriber);
    }


    private boolean validate() {
        if (mEmail.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(mEmail).matches()) {
            mView.inputEmailError(mContext.getString(R.string.wrong_email));
            return false;
        } else {
            mView.inputEmailError(null);
        }

        if (mPassword.isEmpty() || mPassword.length() < 6 || mPassword.length() > 20) {
            mView.inputPasswordError(mContext.getString(R.string.psw_wrong_length));
            return false;
        } else {
            mView.inputPasswordError(null);
        }

        mEditor = mSP.edit();
        if (mIsRemember){
            mEditor.putString("email", mEmail);
            mEditor.putString("password", mPassword);
            mEditor.putBoolean("isRemember", true);
        }else {
            mEditor.clear();
        }
        mEditor.apply();
        return true;
    }


    public void onLoginSuccess(UserBean user){
        try {
            Global.isLogin = true;
            UserBean localUser = mModel.getUserByEmail(mEmail);
            if (user.getImagePath() != null){
                //服务器存在该用户的头像
                if (localUser == null || localUser.getImagePath() == null){
                    //本地数据库不存在，从服务器下载后保存到本地
                    mModel.saveUser(user);
//                    syncImage(user);
                } else {
                    Bitmap bitmap = BitmapFactory.decodeFile(localUser.getImagePath());
                    if (bitmap == null){
                        //本地图片不存在，从服务器下载后保存到本地
//                        syncImage(user);
                    } else {
                        bitmap.recycle();
                        user.setImagePath(localUser.getImagePath());
                        mModel.saveOrUpdateUser(user);
                        mView.finishWithResult(user);
                    }
                }
            } else {
                mModel.saveOrUpdateUser(user);
                mView.finishWithResult(user);
            }
        } catch (Exception e){
            LogUtil.d("----exception: " + e);
        }
    }


    /**
     * 同步用户头像
     */
//    public void syncImage(final UserBean user){
//        DownloadAsyncTask downloadAsyncTask = new DownloadAsyncTask(this);
//        downloadAsyncTask.execute(user.getEmail(), Environment.getExternalStorageDirectory()
//                .getAbsolutePath()+"/"+getString(R.string.app_name)+"/image");
//        downloadAsyncTask.setAsyncResponse(new AsyncResponse() {
//            @Override
//            public void onDataReceivedSuccess(List<String> list) {
//                user.setImagePath(list.get(0));
//                Global.userInfoDB.saveOrUpdate(user);
//                Intent data = new Intent();
//                data.putExtra("user",user);
//                setResult(RESULT_OK, data);
//                finish();
//                Toast.makeText(LoginActivity.this, getString(R.string.synchronized_successfully), Toast.LENGTH_LONG ).show();
//            }
//
//            @Override
//            public void onDataReceivedFailed() {
//                user.setImagePath(null);
//                Global.userInfoDB.saveOrUpdate(user);
//                Intent data = new Intent();
//                data.putExtra("user",user);
//                setResult(RESULT_OK, data);
//                finish();
//                Toast.makeText(LoginActivity.this, getString(R.string.synchronized_failed), Toast.LENGTH_LONG ).show();
//            }
//        });
//    }

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
