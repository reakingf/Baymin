package com.qa.fgj.baymin.model.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.qa.fgj.baymin.model.entity.UserBean;

/**
 * 用户信息数据表操作类
 * Created by FangGengjia on 2017/2/7.
 */
public class UserInfoDBManager extends DBManagerBase {

    private static UserInfoDBManager instance;

    private UserInfoDBManager(Context context) {
        super(context);
        openWriteDB();
    }

    /**
     * 双重同步式单例模式保证只要一个用户信息数据表操作类实例
     */
    public static UserInfoDBManager getInstance(Context context){
        if (instance == null){
            synchronized (LOCK){
                if (instance == null){
                    instance = new UserInfoDBManager(context);
                }
            }
        }
        return instance;
    }

    public void saveOrUpdate(UserBean user){
        if (user == null){
            return;
        }
        if (emailIsExist(user.getEmail())){
            update(user);
        }else {
            save(user);
        }
    }


    /**
     * 保存用户信息
     */
    public void save(UserBean user){
        synchronized (LOCK){
            openWriteDB();
//            ContentValues values = createContentValues(user);
            ContentValues values = new ContentValues();
//            values.put(UserBean.Table.FIELD_ID, user.getId());
            values.put(UserBean.Table.FIELD_USER_NAME, user.getUsername());
            values.put(UserBean.Table.FIELD_EMAIL, user.getEmail());
            values.put(UserBean.Table.FIELD_PASSWORD, user.getPassword());
            values.put(UserBean.Table.FIELD_IMG_URL, user.getImagePath());
            long id = mDataBase.insert(UserBean.Table.TABLE_NAME, null, values);
//            LogUtil.d(Global.appContext, "-------save: id = "+id);
        }
    }

    private ContentValues createContentValues(UserBean user){
        ContentValues values = new ContentValues();
        values.put(UserBean.Table.FIELD_ID, user.getId());
        values.put(UserBean.Table.FIELD_USER_NAME, user.getUsername());
        values.put(UserBean.Table.FIELD_EMAIL, user.getEmail());
        values.put(UserBean.Table.FIELD_PASSWORD, user.getPassword());
        values.put(UserBean.Table.FIELD_IMG_URL, user.getImagePath());
        return values;
    }

    public UserBean queryByEmail(String email){
        if (email == null){
            return null;
        }
        synchronized (LOCK){
            openWriteDB();
            mDataBase.beginTransaction();
            UserBean user = new UserBean();
            Cursor cursor;
            cursor = mDataBase.query(UserBean.Table.TABLE_NAME, null, UserBean.Table.FIELD_EMAIL + " =? ",
                    new String[]{email}, null, null, null);
            if (cursor != null && cursor.moveToFirst()){
                user.setId(cursor.getInt(cursor.getColumnIndex(UserBean.Table.FIELD_ID)));
                user.setUsername(cursor.getString(cursor.getColumnIndex(UserBean.Table.FIELD_USER_NAME)));
                user.setEmail(cursor.getString(cursor.getColumnIndex(UserBean.Table.FIELD_EMAIL)));
                user.setPassword(cursor.getString(cursor.getColumnIndex(UserBean.Table.FIELD_PASSWORD)));
                user.setImagePath(cursor.getString(cursor.getColumnIndex(UserBean.Table.FIELD_IMG_URL)));
                cursor.close();
            } else {
                user = null;
            }
            mDataBase.setTransactionSuccessful();
            mDataBase.endTransaction();
            return user;
        }
    }

    public UserBean queryByAccount(String account){
        if (account == null){
            return null;
        }
        synchronized (LOCK){
            openWriteDB();
            mDataBase.beginTransaction();
            UserBean user = new UserBean();
            Cursor cursor;
            cursor = mDataBase.query(UserBean.Table.TABLE_NAME, null, UserBean.Table.FIELD_USER_NAME + " =? ",
                    new String[]{account}, null, null, null);
            if (cursor != null && cursor.moveToFirst()){
                user.setId(cursor.getInt(cursor.getColumnIndex(UserBean.Table.FIELD_ID)));
                user.setUsername(cursor.getString(cursor.getColumnIndex(UserBean.Table.FIELD_USER_NAME)));
                user.setEmail(cursor.getString(cursor.getColumnIndex(UserBean.Table.FIELD_EMAIL)));
                user.setPassword(cursor.getString(cursor.getColumnIndex(UserBean.Table.FIELD_PASSWORD)));
                user.setImagePath(cursor.getString(cursor.getColumnIndex(UserBean.Table.FIELD_IMG_URL)));
                cursor.close();
            } else {
                user = null;
            }
            mDataBase.setTransactionSuccessful();
            mDataBase.endTransaction();
            return user;
        }
    }

    public boolean emailIsExist(String email){
        return queryByEmail(email) != null;
    }

    /**
     * 更新用户信息
     */
    public void update(UserBean user){
        synchronized (LOCK){
            openWriteDB();
            mDataBase.beginTransaction();
            ContentValues values = createContentValues(user);
            int affectedRow = mDataBase.update(UserBean.Table.TABLE_NAME, values, UserBean.Table.FIELD_ID + " =? ",
                    new String[]{String.valueOf(user.getId().intValue())});
//            LogUtil.d(Global.appContext, "-------update: affected row = "+affectedRow);
            mDataBase.setTransactionSuccessful();
            mDataBase.endTransaction();
        }
    }

    /**
     * 删除用户信息，暂不考虑
     */
//    public void deleteUserInfo(int id){
//        synchronized (LOCK){
//            openWriteDB();
//        }
//    }

    @Override
    public void closeDB() {
        super.closeDB();
        instance = null;
        mDBHelper = null;
    }
}
