package com.qa.fgj.baymin.ui.view;

import com.qa.fgj.baymin.base.IBaseView;
import com.qa.fgj.baymin.model.entity.MessageBean;

import java.util.List;

/**
 * Created by FangGengjia on 2017/2/19.
 */

public interface IMainView extends IBaseView {

    void initListViewData(List<MessageBean> msgList);

}
