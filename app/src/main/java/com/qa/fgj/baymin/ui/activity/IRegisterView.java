package com.qa.fgj.baymin.ui.activity;

import com.qa.fgj.baymin.base.IBaseView;
import com.qa.fgj.baymin.model.entity.UserBean;

/**
 * Created by FangGengjia on 2017/2/18.
 */

public interface IRegisterView extends IBaseView {

    void inputNameError(String msg);

    void inputEmailError(String msg);

    void inputPasswordError(String msg);

    void inputConfirmPasswordError(String msg);

    void showProgressDialog();

    void dismissProgressDialog();

    void onSignUpFailed(String tips);

    void onSignUpSuccess(UserBean userBean);

}
