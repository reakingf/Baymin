package com.qa.fgj.baymin.presenter;

import com.qa.fgj.baymin.base.IBasePresenter;
import com.qa.fgj.baymin.base.IBaseView;
import com.qa.fgj.baymin.model.PersonalModel;
import com.qa.fgj.baymin.model.entity.UserBean;
import com.qa.fgj.baymin.ui.view.IPersonalInfoView;
import com.qa.fgj.baymin.util.Global;

import rx.Observable;

/**
 * Created by FangGengjia on 2017/2/19.
 */

public class PersonalInfoPresenter<T extends IPersonalInfoView> implements IBasePresenter<T> {

    private T view;
    private PersonalModel model;

    @Override
    public void onCreate() {
        model = new PersonalModel();
    }

    @Override
    public void attachView(T view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        view = null;
    }

    @Override
    public void onDestroy() {

    }

    public void fetchData(String account) {
        UserBean userBean = Global.userInfoDB.queryByAccount(account);
        view.showData(userBean);
    }
}
