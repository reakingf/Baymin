package com.qa.fgj.baymin.ui.activity;

import com.qa.fgj.baymin.base.IBaseView;
import com.qa.fgj.baymin.model.entity.MessageBean;

import java.util.List;

/**
 * Created by FangGengjia on 2017/2/10.
 */

public interface ICommunicationView extends IBaseView {

    void initListViewData(List<MessageBean> msgList);

}
