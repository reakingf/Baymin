package com.qa.fgj.baymin.model.entity;

/**
 * Created by FangGengjia on 2017/3/14.
 */

public class BayMinResponse<T> {

    int code;
    int statusCode;
    boolean isSuccessed;
    String message;
    T content;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public boolean isSuccessed() {
        return isSuccessed;
    }

    public void setSuccessed(boolean successed) {
        isSuccessed = successed;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }
}
