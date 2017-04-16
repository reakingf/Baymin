package com.qa.fgj.baymin.net.api;

import com.qa.fgj.baymin.model.entity.BayMinResponse;
import com.qa.fgj.baymin.model.entity.weather.WeatherRoot;

import retrofit2.http.GET;
import rx.Observable;

/**
 * 天气预报相关API
 * Created by FangGengjia on 2017/4/12.
 */

public interface WeatherApi {

    @GET("BayminServlet/GetWeatherData")
    Observable<BayMinResponse<WeatherRoot>> getWeatherData();

}
