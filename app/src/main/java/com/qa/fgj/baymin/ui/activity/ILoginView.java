package com.qa.fgj.baymin.ui.activity;

import com.qa.fgj.baymin.base.IBaseView;
import com.qa.fgj.baymin.model.entity.UserBean;

/**
 * Created by FangGengjia on 2017/2/18.
 */

public interface ILoginView extends IBaseView {

    void bindData(String email, String password, boolean isRemember);

    void inputEmailError(String msg);

    void inputPasswordError(String msg);

    void showProgressDialog();

    void dismissProgressDialog();

    void onLoginFailed(String tips);

    void onLoginSuccess(UserBean userBean);

    void finishWithResult(UserBean user);

}
