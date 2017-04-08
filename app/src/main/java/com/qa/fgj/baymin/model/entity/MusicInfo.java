package com.qa.fgj.baymin.model.entity;

/**
 * 音乐信息实体类
 * Created by FangGengjia on 2017/4/6.
 */

public class MusicInfo {

    //    private  long id;
    private String title;
    //    private long duration;
    private String url;

    public MusicInfo() {

    }

//    public MusicInfo(String title, String url) {
//        this.title = title;
//        this.url = url;
//    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


}
