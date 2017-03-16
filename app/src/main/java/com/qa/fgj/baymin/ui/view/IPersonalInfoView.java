package com.qa.fgj.baymin.ui.view;

import com.qa.fgj.baymin.base.IBaseView;
import com.qa.fgj.baymin.model.entity.UserBean;

/**
 * Created by FangGengjia on 2017/2/19.
 */

public interface IPersonalInfoView extends IBaseView{

    void showData(UserBean userBean);
}
