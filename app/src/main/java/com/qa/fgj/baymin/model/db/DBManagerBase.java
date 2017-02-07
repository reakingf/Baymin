package com.qa.fgj.baymin.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * 数据库操作基类
 * Created by FangGengjia on 2017/2/7.
 *
 */
public class DBManagerBase {

    private static final String DB_NAME = "baymin";
    public static final int VERSION = 1;
    protected static  DataBaseOpenHelper mDBHelper;
    protected static SQLiteDatabase mDataBase;
    /** 加锁解决 并发问题 **/
    protected static final Object LOCK = new Object();

    protected DBManagerBase(Context context){
        if (mDBHelper == null){
            synchronized (LOCK){
                if (mDBHelper == null){
                    mDBHelper = new DataBaseOpenHelper(context, DB_NAME, null, VERSION);
                }
            }
        }
    }

    protected static void openWriteDB(){
        if (mDataBase == null){
            mDataBase = mDBHelper.getWritableDatabase();
        }
    }

    public void closeDB(){
        if (mDataBase != null && mDataBase.isOpen()){
            mDataBase.close();
            mDataBase = null;
        }
        mDBHelper = null;
    }

}
