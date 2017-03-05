package com.qa.fgj.baymin.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.qa.fgj.baymin.model.entity.IntroductionBean;
import com.qa.fgj.baymin.model.entity.MessageBean;
import com.qa.fgj.baymin.model.entity.UserBean;

/**
 * 数据库管理类
 * Created by FangGengjia on 2017/2/7.
 *
 */
public class DataBaseOpenHelper extends SQLiteOpenHelper {


    public DataBaseOpenHelper(Context context, String name,
                              SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(UserBean.Table.createTableSql());
        db.execSQL(MessageBean.Table.createTableSql());
        db.execSQL(IntroductionBean.Table.createTableSql());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(UserBean.Table.createTableSql());
        db.execSQL(MessageBean.Table.createTableSql());
        db.execSQL(IntroductionBean.Table.createTableSql());
    }

}
