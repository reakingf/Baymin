package com.qa.fgj.baymin.model.entity;

import java.io.Serializable;

/**
 * Created by FangGengjia on 2017/2/7.
 * 用户个人信息实体类
 */
public class UserBean implements Serializable{

    private static final long serialVersionUID = 45646124852342L;

    private Integer id;
    private String username;
    private String email;
    private String password;
    private String imagePath;
    private String growthValue;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getGrowthValue() {
        return growthValue;
    }

    public void setGrowthValue(String growthValue) {
        this.growthValue = growthValue;
    }

    public static final class Table {
        /** 用户信息表 **/
        public static final String TABLE_NAME = "UserInfoTable";
        /** 用户id **/
        public static final String FIELD_ID = "id";
        /** 用户名 **/
        public static final String FIELD_USER_NAME = "user_name";
        /** 用户邮箱 **/
        public static final String FIELD_EMAIL = "email";
        /** 用户密码 **/
        public static final String FIELD_PASSWORD = "password";
        /** 用户头像路径 */
        public static final String FIELD_IMG_URL = "user_img_url";
        /** 成长值 */
        public static final String FIELD_GROWTH_VALUE = "user_growth_value";

        /**
         * 创建用户信息表的sql语句
         */
        public static String createTableSql(){
            StringBuilder sb = new StringBuilder();
            sb.append("create table if not exists ")
                    .append(TABLE_NAME)
                    .append("( " + FIELD_ID + " integer primary key autoincrement, ")
                    .append(FIELD_USER_NAME + " text , ")
                    .append(FIELD_EMAIL + " text,")
                    .append(FIELD_PASSWORD + " text,")
                    .append(FIELD_GROWTH_VALUE + " text,")
                    .append(FIELD_IMG_URL + " text)");
            return sb.toString();
        }
    }

}
