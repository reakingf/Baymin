package com.qa.fgj.baymin.model.entity;

import java.io.Serializable;

/**
 * Created by FangGengjia on 2017/3/5.
 */

public class IntroductionBean implements Serializable {

    private static final long serialVersionUID = 23616184950342L;

    public IntroductionBean() {
    }

    public IntroductionBean(String title, String content) {
        this.title = title;
        this.content = content;
    }

    String title;
    String content;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public static final class Table{
        public static final String TABLE_NAME = "IntroductionTable";
        public static final String FIELD_ID = "id";
        public static final String FIELD_TITLE = "email";
        public static final String FIELD_CONTENT = "password";

        /**
         * 创建用户信息表的sql语句
         */
        public static String createTableSql(){
            StringBuilder sb = new StringBuilder();
            sb.append("create table if not exists ")
                    .append(TABLE_NAME)
                    .append("( " + FIELD_ID + " integer primary key autoincrement, ")
                    .append(FIELD_TITLE + " text,")
                    .append(FIELD_CONTENT + " text)");
            return sb.toString();
        }
    }

}
