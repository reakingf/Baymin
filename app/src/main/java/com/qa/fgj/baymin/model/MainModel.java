package com.qa.fgj.baymin.model;

import android.content.Context;

import com.qa.fgj.baymin.model.entity.MessageBean;
import com.qa.fgj.baymin.net.RestApiService;
import com.qa.fgj.baymin.net.api.CommunicationApi;
import com.qa.fgj.baymin.util.Global;
import com.qa.fgj.baymin.util.LogUtil;
import com.qa.fgj.baymin.util.SharPreferencesDB;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by FangGengjia on 2017/2/18.
 */

public class MainModel {

    final CommunicationApi communicationApi;

    private Context mContext;
    private SharPreferencesDB preferencesDB;

    public MainModel(Context context) {
        this.mContext = context;
        preferencesDB = new SharPreferencesDB(mContext, "loginInfo");
        communicationApi = RestApiService.getInstance().createApi(CommunicationApi.class);
    }

    public Observable<Boolean> getLoginCache(){
        boolean shouldLogin = preferencesDB.getBoolean("isRemember");
        return Observable.just(shouldLogin);
    }

    public Observable<MessageBean> getAnswer(String question){
        return communicationApi.getRespond(question);
    }

    /**
     * @param startId 查询的其实ID
     * @param offset 偏移量
     * @return Observable类型的消息列表
     */
    public Observable<List<MessageBean>> loadData(String startId, int offset) {
        List<MessageBean> list = Global.messageDB.queryByMsgIdDESC(startId, offset);

        if (list != null){
            return Observable.just(list);
        }
        return Observable.just((List<MessageBean>) new ArrayList<MessageBean>());
    }

    public Observable<Boolean> saveData(MessageBean messageBean){
        try{
            if (Global.messageDB == null){
                Global.initDB();
            }
            Global.messageDB.save(messageBean);
        } catch (Exception e){
            e.printStackTrace();
            Observable.just(false);
        }
        return Observable.just(true);
    }

}
