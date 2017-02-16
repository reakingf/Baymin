package com.qa.fgj.baymin.util;

import android.content.Context;

import com.qa.fgj.baymin.model.db.MessageDBManager;
import com.qa.fgj.baymin.model.db.UserInfoDBManager;

/**
 * Created by FangGengjia on 2017/2/8.
 * 全局变量声明类
 */
public class Global {

    public static Context appContext;

    /* 用户信息数据库 */
    public static UserInfoDBManager userInfoDB;
    /* 聊天消息数据库 */
    public static MessageDBManager messageDB;

    /* 登录状态 */
    public static boolean isLogin;

    /**
     * 初始化所有数据库，获取数据库实例
     */
    public static void initDB(){
        userInfoDB = UserInfoDBManager.getInstance(appContext);
        messageDB = MessageDBManager.getInstance(appContext);
    }

    /**
     * 关闭数据库连接
     */
    public static void closeDB(){
        if (userInfoDB != null){
            userInfoDB.closeDB();
            userInfoDB = null;
        }
        if (messageDB != null){
            messageDB.closeDB();
            messageDB = null;
        }
    }
}
