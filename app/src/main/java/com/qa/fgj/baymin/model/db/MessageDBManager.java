package com.qa.fgj.baymin.model.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.qa.fgj.baymin.model.entity.MessageBean;

import java.util.ArrayList;

/**
 * 聊天消息表操作类
 * Created by FangGengjia on 2017/2/7.
 */
public class MessageDBManager extends DBManagerBase {

    private static MessageDBManager instance;
    private Context context;

    private MessageDBManager(Context context) {
        super(context);
        this.context = context;
        openWriteDB();
    }

    public static MessageDBManager getInstance(Context context){
        if (instance == null){
            synchronized (LOCK){
                if (instance == null){
                    instance = new MessageDBManager(context);
                }
            }
        }
        return instance;
    }

    private ContentValues createContentValues(MessageBean messageBean){
        ContentValues values = new ContentValues();
//        values.put(MessageBean.Table.FIELD_ID, messageBean.getId());
        values.put(MessageBean.Table.FIELD_MSG_CONTENT, messageBean.getContent());
        if (messageBean.isSendMsg) {
            values.put(MessageBean.Table.FIELD_IS_SEND_MSG, "true");
        } else {
            values.put(MessageBean.Table.FIELD_IS_SEND_MSG, "false");
        }
        values.put(MessageBean.Table.FIELD_CREATE_TINE, messageBean.getCreateTime());
        return values;
    }

    /**
     * 保存聊天消息记录
     */
    public void save(MessageBean messageBean){
        if (messageBean == null){
            return;
        }
        synchronized (LOCK){
            openWriteDB();
            ContentValues values = createContentValues(messageBean);
            long id = mDataBase.insert(MessageBean.Table.TABLE_NAME, null, values);
//            LogUtil.d(Global.appContext, "-------insert id = "+id);
        }
    }

    /**
     * 加载历史聊天记录
     */
    public ArrayList<MessageBean> query(){
        synchronized (LOCK){
            openWriteDB();
            return query(null, null, null);
        }
    }

    /** 根据id字段获取指定大小(20个)的聊天记录（时间降序） */
    public ArrayList<MessageBean> queryByMsgToIdDESC(String msgId, int offset) {
        synchronized (LOCK) {
            openWriteDB();
            String order1 = MessageBean.Table.FIELD_CREATE_TINE + " DESC, ";
            String order2 = MessageBean.Table.FIELD_ID + " limit 20 offset " + offset;
            return query(MessageBean.Table.FIELD_ID + ">=?", new String[] { msgId }, order1 + order2);
        }
    }

    private ArrayList<MessageBean> query(String where, String[] param, String order) {
        synchronized (LOCK) {
            openWriteDB();
            ArrayList<MessageBean> beans = null;
            Cursor cur = null;
            try {
                cur = mDataBase.query(MessageBean.Table.TABLE_NAME, null, where, param, null, null, order);
                beans = new ArrayList<>();
                if (cur != null && cur.getCount() > 0) {
                    while (cur.moveToNext()) {
                        beans.add(createBean(cur));
                    }
                }
            } catch (Exception e) {
//                LogUtil.e(context, "query:查询失败：" + where + "," + e.getMessage());
            }
            finally {
                if (cur != null) {
                    cur.close();
                }
            }
            return beans;
        }
    }

    /**
     * 根据游标指向的一条记录生成一个实体对象
     */
    private MessageBean createBean(Cursor cur) {
        int id = cur.getInt(cur.getColumnIndexOrThrow(MessageBean.Table.FIELD_ID));
        String mContent = cur.getString(cur.getColumnIndexOrThrow(MessageBean.Table.FIELD_MSG_CONTENT));
        boolean isSendMsg = ("true".equals(cur.getString(cur.getColumnIndexOrThrow(MessageBean.Table.FIELD_IS_SEND_MSG))));
        long createTime = cur.getLong(cur.getColumnIndexOrThrow(MessageBean.Table.FIELD_CREATE_TINE));
        return new MessageBean(id, mContent, isSendMsg, createTime);
    }

    /**
     * 删除单条消息记录
     * @param msgId 待删除消息ID
     */
    public void delete(int msgId){
        synchronized (LOCK){
            int[] msgIds = new int[1];
            msgIds[0] = msgId;
            deleteMessage(msgIds);
        }
    }

    /**
     * 删除多条消息记录
     * @param msgIds 待删除消息ID
     */
    public void deleteMessage(int[] msgIds){
        if (msgIds == null || msgIds.length == 0){
            return;
        }
        synchronized (LOCK){
            int length = msgIds.length;
            String[] str_MsgIds = new String[length];
            for (int i = 0; i < length; i++) {
                str_MsgIds[i] = String.valueOf(msgIds[i]);
            }
            openWriteDB();
            mDataBase.delete(MessageBean.Table.TABLE_NAME, MessageBean.Table.FIELD_ID + " =? ", str_MsgIds);
        }
    }

    /**
     * 清空所有消息记录
     */
    public void deleteAll(){
        synchronized (LOCK){
            openWriteDB();
            mDataBase.delete(MessageBean.Table.TABLE_NAME, null, null);
        }
    }


    @Override
    public void closeDB() {
        super.closeDB();
        instance = null;
    }
}
