package com.qa.fgj.baymin.model.entity.weather;

import java.util.List;

/**
 *
 * Created by FangGengjia on 2017/4/12.
 */

public class Results {

    private String currentCity;

    private String pm25;

    private List<Index> index ;

    private List<Weather_data> weather_data ;

    public String getCurrentCity() {
        return currentCity;
    }

    public void setCurrentCity(String currentCity) {
        this.currentCity = currentCity;
    }

    public String getPm25() {
        return pm25;
    }

    public void setPm25(String pm25) {
        this.pm25 = pm25;
    }

    public List<Index> getIndex() {
        return index;
    }

    public void setIndex(List<Index> index) {
        this.index = index;
    }

    public List<Weather_data> getWeather_data() {
        return weather_data;
    }

    public void setWeather_data(List<Weather_data> weather_data) {
        this.weather_data = weather_data;
    }
}
