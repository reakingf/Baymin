package com.qa.fgj.baymin.presenter;

import com.qa.fgj.baymin.base.IBaseModule;
import com.qa.fgj.baymin.base.IBasePresenter;
import com.qa.fgj.baymin.base.IBaseView;
import com.qa.fgj.baymin.model.entity.MessageBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by FangGengjia on 2017/1/29
 */

public class CommunicationPresenter implements IBasePresenter {

    private IBaseView view;
    private IBaseModule module;

    @Override
    public void attachView(IBaseView view) {
        this.view = view;
    }

    public List<MessageBean> loadDataFromDB(){
        List<MessageBean> list = new ArrayList<>();
        MessageBean messageBean = new MessageBean("你好，我叫小白", false, 100031);
        MessageBean messageBean1 = new MessageBean("你好，小白，很高兴认识你", true, 1111020);
        list.add(messageBean);
        list.add(messageBean1);
        return list;
    }



    @Override
    public void detachView() {

    }
}
