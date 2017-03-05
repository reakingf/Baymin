package com.qa.fgj.baymin.model.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.qa.fgj.baymin.model.entity.IntroductionBean;
import com.qa.fgj.baymin.model.entity.MessageBean;
import com.qa.fgj.baymin.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by FangGengjia on 2017/3/5.
 */

public class IntroductionDBManager extends DBManagerBase {

    private static IntroductionDBManager instance;
    private Context context;

    private IntroductionDBManager(Context context) {
        super(context);
        this.context = context;
        openWriteDB();
    }

    public static IntroductionDBManager getInstance(Context context){
        if (instance == null){
            synchronized (LOCK){
                if (instance == null){
                    instance = new IntroductionDBManager(context);
                }
            }
        }
        return instance;
    }

    private ContentValues createContentValues(IntroductionBean introductionBean){
        ContentValues values = new ContentValues();
        values.put(IntroductionBean.Table.FIELD_TITLE, introductionBean.getTitle());
        values.put(IntroductionBean.Table.FIELD_TITLE, introductionBean.getTitle());
        return values;
    }

    /**
     * 保存聊天消息记录
     */
    public void save(IntroductionBean introductionBean){
        if (introductionBean == null){
            return;
        }
        synchronized (LOCK){
            openWriteDB();
            ContentValues values = createContentValues(introductionBean);
            long id = mDataBase.insert(MessageBean.Table.TABLE_NAME, null, values);
            LogUtil.d("-------insert id = "+id);
        }
    }

    public List<IntroductionBean> load(){
        synchronized (LOCK){
            openWriteDB();
            ArrayList<IntroductionBean> beans = null;
            Cursor cur = null;
            try {
                cur = mDataBase.query(MessageBean.Table.TABLE_NAME, null, null, null, null, null, null);
                beans = new ArrayList<>();
                if (cur != null && cur.getCount() > 0) {
                    while (cur.moveToNext()) {
                        beans.add(createBean(cur));
                    }
                }
            } catch (Exception e) {
                LogUtil.e("MessageDBManager", "query:查询失败：" + e.getMessage());
            }
            finally {
                if (cur != null) {
                    cur.close();
                }
            }
            return beans;
        }
    }

    private IntroductionBean createBean(Cursor cur) {
//        int id = cur.getInt(cur.getColumnIndexOrThrow(IntroductionBean.Table.FIELD_ID));
        String title = cur.getString(cur.getColumnIndexOrThrow(IntroductionBean.Table.FIELD_TITLE));
        String content = cur.getString(cur.getColumnIndexOrThrow(IntroductionBean.Table.FIELD_CONTENT));
        return new IntroductionBean(title, content);
    }

}
