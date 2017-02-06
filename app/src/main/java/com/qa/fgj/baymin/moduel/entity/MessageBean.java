package com.qa.fgj.baymin.moduel.entity;

/**
 * Created by FangGengjia on 2017/1/19.
 * 聊天消息实体类,聊天消息仅保存在客户端，不保存到服务器
 */
public class MessageBean {

    private Integer id;
    private String content;
    public boolean isSendMsg;
    public static final boolean TYPE_SEND = true;
    public static final boolean TYPE_RECEIVED = false;
    /** 是否显示时间，与上一条消息相隔10分钟以内的不显示消息时间，首次加载聊天界面时最近的一条显示时间 */
    public boolean shouldShowCreateTime;
    private long createTime;

    public MessageBean(){

    }

    public MessageBean(String content, boolean isSendMsg, long createTime) {
        this.content = content;
        this.isSendMsg = isSendMsg;
        this.createTime = createTime;
    }

    public MessageBean(Integer id, String content, boolean isSendMsg, long createTime) {
        this.id = id;
        this.content = content;
        this.isSendMsg = isSendMsg;
        this.createTime = createTime;
    }

    public int getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public static final class Table {
        /** 聊天消息记录表 **/
        public static final String TABLE_NAME = "MessageTable";
        /** 消息编号 **/
        public static final String FIELD_ID = "id";
        /** 因为默认服务器是即时恢复，所以只记录用户发送消息时间 **/
        public static final String FIELD_CREATE_TINE = "create_time";
        /** 发出消息 **/
        public static final String FIELD_MSG_CONTENT = "content";
        /** 收到消息 **/
        public static final String FIELD_IS_SEND_MSG = "is_send";

        /**
         * 创建聊天消息记录表的sql语句
         */
        public static String createTableSql(){
            StringBuilder sb = new StringBuilder();
            sb.append("create table if not exists ")
                    .append(TABLE_NAME)
                    .append("( " + FIELD_ID + " integer primary key autoincrement, ")
                    .append(FIELD_MSG_CONTENT + " text,")
                    .append(FIELD_CREATE_TINE + " integer,")
                    .append(FIELD_IS_SEND_MSG + " text)");
            return sb.toString();
        }

    }

}
