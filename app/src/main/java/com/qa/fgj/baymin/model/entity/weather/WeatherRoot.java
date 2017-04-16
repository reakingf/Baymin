package com.qa.fgj.baymin.model.entity.weather;

import java.util.List;

/**
 * 天气预报信息根实体类
 * Created by FangGengjia on 2017/4/12.
 */

public class WeatherRoot{

    private int error;

    private String status;

    private String date;

    private List<Results> results ;

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<Results> getResults() {
        return results;
    }

    public void setResults(List<Results> results) {
        this.results = results;
    }
}
