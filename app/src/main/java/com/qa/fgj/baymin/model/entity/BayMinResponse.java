package com.qa.fgj.baymin.model.entity;

/**
 * Created by FangGengjia on 2017/3/14.
 */

public class BayMinResponse<T> {

    private int code;
    private boolean isSucceed;
    private String message;
    private T content;

    public void setSucceed(boolean succeed) {
        isSucceed = succeed;
    }

    private long createTime;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isSucceed() {
        return isSucceed;
    }

    public void setSuccessed(boolean succeed) {
        isSucceed = succeed;
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

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "code = " + code
                + ", isSucceed = " + isSucceed
                + ", message = " + message
                + ", content：{ " + content.toString() + "}";
    }
}
