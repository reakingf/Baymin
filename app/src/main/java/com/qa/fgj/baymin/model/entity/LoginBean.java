package com.qa.fgj.baymin.model.entity;

/**
 * Created by FangGengjia on 2017/2/19.
 */

public class LoginBean {

    private String email;
    private String password;
    private boolean isRemember;

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

    public boolean isRemember() {
        return isRemember;
    }

    public void setRemember(boolean remember) {
        isRemember = remember;
    }
}
