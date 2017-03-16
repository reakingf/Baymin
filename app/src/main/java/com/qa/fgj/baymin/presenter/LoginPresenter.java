package com.qa.fgj.baymin.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.qa.fgj.baymin.R;
import com.qa.fgj.baymin.base.IBasePresenter;
import com.qa.fgj.baymin.model.LoginModel;
import com.qa.fgj.baymin.model.entity.BayMinResponse;
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
import static android.util.Patterns.EMAIL_ADDRESS;

/**
 * Created by FangGengjia on 2017/2/18.
 */

public class LoginPresenter<T extends ILoginView> implements IBasePresenter<T> {

    private Context context;
    private T view;
    private LoginModel model;
    private Scheduler uiThread;
    private Scheduler backgroundThread;

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private String mEmail;
    private String mPassword;
    private boolean mIsRemember;
    private Subscription subscription;

    public LoginPresenter(Context context, Scheduler uiThread, Scheduler backgroundThread) {
        this.context = context;
        this.uiThread = uiThread;
        this.backgroundThread = backgroundThread;
    }

    @Override
    public void onCreate() {
        model = new LoginModel();
    }

    @Override
    public void attachView(T view) {
        this.view = view;
    }

    public void fetchCache(){
        sp = context.getSharedPreferences("loginInfo", MODE_PRIVATE);
        view.bindData(sp.getString("email",""), sp.getString("password",""),
                sp.getBoolean("isRemember", false));
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

    public void login(Subscriber<BayMinResponse<UserBean>> subscriber){

        if (!SystemUtil.isNetworkConnected()){
            view.onLoginFailed("网络不可用，请检查网络");
            return;
        }

        if (!validate()) {
            view.onLoginFailed(null);
            return;
        }

        view.showProgressDialog();
        subscription = model.login(mEmail, MD5Util.getMD5Digest(mPassword))
                .subscribeOn(backgroundThread)
                .observeOn(uiThread)
                .subscribe(subscriber);
    }


    private boolean validate() {
        if (mEmail == null || !EMAIL_ADDRESS.matcher(mEmail).matches()) {
            view.inputEmailError(context.getString(R.string.wrong_email));
            return false;
        }
//        else {
//            view.inputEmailError(null);
//        }

        if (mPassword == null || mPassword.length() < 6 || mPassword.length() > 20) {
            view.inputPasswordError(context.getString(R.string.psw_wrong_length));
            return false;
        }
//        else {
//            view.inputPasswordError(null);
//        }
        return true;
    }

    /**
     * 缓存登录信息
     */
    public void saveLoginCache() {
        if (mIsRemember){
            editor = sp.edit();
            editor.putString("email", mEmail);
            editor.putString("password", mPassword);
            editor.putBoolean("isRemember", true);
            editor.apply();
        }
    }

    public void onLoginSuccess(UserBean user){
        try {
            Global.isLogin = true;
            UserBean localUser = model.getUserByEmail(mEmail);
            if (user.getImagePath() != null){
                //服务器存在该用户的头像
                if (localUser == null || localUser.getImagePath() == null){
                    //本地图片不存在，从服务器下载并返回图片保存地址
//                   user.setImagePath(syncImage(user));
                } else {
                    Bitmap bitmap = BitmapFactory.decodeFile(localUser.getImagePath());
                    if (bitmap == null){
                        //本地路径找不到图片，从服务器下载并保存到本地
//                      user.setImagePath(syncImage(user));
                    } else {
                        bitmap.recycle();
                        user.setImagePath(localUser.getImagePath());
                    }
                }
            }
            model.saveOrUpdateUser(user);
            view.finishWithResult(user);
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
        view = null;
    }

    @Override
    public void onDestroy() {
        if (subscription != null && !subscription.isUnsubscribed()){
            subscription.unsubscribe();
        }
    }

}
