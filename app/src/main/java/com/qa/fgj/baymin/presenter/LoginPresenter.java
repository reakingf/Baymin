package com.qa.fgj.baymin.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.qa.fgj.baymin.R;
import com.qa.fgj.baymin.app.Constant;
import com.qa.fgj.baymin.base.IBasePresenter;
import com.qa.fgj.baymin.model.LoginModel;
import com.qa.fgj.baymin.model.entity.BayMinResponse;
import com.qa.fgj.baymin.model.entity.UserBean;
import com.qa.fgj.baymin.ui.view.ILoginView;
import com.qa.fgj.baymin.util.Global;
import com.qa.fgj.baymin.util.LogUtil;
import com.qa.fgj.baymin.util.MD5Util;
import com.qa.fgj.baymin.util.SystemUtil;
import com.qa.fgj.baymin.util.ToastUtil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Func1;

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
    private List<Subscription> subscriptionList = new ArrayList<>();

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

        view.showProgressDialog(context.getString(R.string.login_validating));
        Subscription subscription = model.login(mEmail, MD5Util.getMD5Digest(mPassword))
                .subscribeOn(backgroundThread)
                .observeOn(uiThread)
                .subscribe(subscriber);
        subscriptionList.add(subscription);
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

    public void onLoginSuccess(final UserBean user){
        Subscriber<String> subscriber = new Subscriber<String>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                view.dismissProgressDialog();
                ToastUtil.shortShow(e.getMessage());
            }

            @Override
            public void onNext(String imaPath) {
                view.dismissProgressDialog();
                if (imaPath != null) {
                    user.setImagePath(imaPath);
                }
                model.saveOrUpdateUser(user);
                view.finishWithResult(user);
            }
        };

        try {
            Global.isLogin = true;
            UserBean localUser = model.getUserByEmail(mEmail);
            if (user.getImagePath() != null){
                //服务器存在该用户的头像
                if (localUser == null || localUser.getImagePath() == null){
                    //本地图片不存在，从服务器下载并返回图片保存地址
                    syncImage(user.getEmail(), subscriber);
                } else {
                    Bitmap bitmap = BitmapFactory.decodeFile(localUser.getImagePath());
                    if (bitmap == null){
                        //本地路径找不到图片，从服务器下载并保存到本地
                        syncImage(user.getEmail(), subscriber);
                    } else {
                        bitmap.recycle();
                        user.setImagePath(localUser.getImagePath());
                        model.saveOrUpdateUser(user);
                        view.finishWithResult(user);
                    }
                }
            }
        } catch (Exception e){
            LogUtil.d("----exception: " + e.getMessage());
        }

    }

    /**
     * 下载头像保存
     */
    public void syncImage(String email, Subscriber<String> subscriber){
        Subscription subscription = model.syncAvatar(email)
                .subscribeOn(backgroundThread)
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        view.showProgressDialog("信息同步中");
                    }
                })
                .subscribeOn(uiThread)
                .observeOn(uiThread)
                .map(new Func1<ResponseBody, String>() {
                    @Override
                    public String call(ResponseBody responseBody) {
                        return downloadImage(responseBody);
                    }
                }).subscribe(subscriber);
        subscriptionList.add(subscription);
    }

    private String downloadImage(ResponseBody body) {
        String imgPath = null;
        try {
            LogUtil.d("start writing image into disk");
            InputStream in = null;
            BufferedInputStream bis = null;
            FileOutputStream fos = null;
            try {
                in = body.byteStream();
                bis = new BufferedInputStream(in);
                byte[] buffer = new byte[1024];
                imgPath = Constant.PATH_IMAGE + File.separator + System.currentTimeMillis() + ".jpg";
                fos = new FileOutputStream(imgPath);
                int len;
                while ((len = bis.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
            } catch (IOException e) {
                LogUtil.d(e.getMessage());
                e.printStackTrace();
            } finally {
                if (bis != null) {
                    bis.close();
                }
                if (in != null) {
                    in.close();
                }
                if (fos != null) {
                    fos.flush();
                    fos.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.d(e.getMessage());
        }
        LogUtil.d("---" + imgPath);
        return imgPath;
    }

    @Override
    public void detachView() {
        view = null;
    }

    @Override
    public void onDestroy() {
        if (subscriptionList != null && subscriptionList.size() > 0){
            for (Subscription subscription : subscriptionList) {
                if (subscription != null && !subscription.isUnsubscribed()){
                    subscription.unsubscribe();
                }
            }
        }
    }

}
