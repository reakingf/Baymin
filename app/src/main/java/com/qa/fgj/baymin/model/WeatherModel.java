package com.qa.fgj.baymin.model;

import com.qa.fgj.baymin.model.entity.BayMinResponse;
import com.qa.fgj.baymin.model.entity.weather.WeatherRoot;
import com.qa.fgj.baymin.net.RestApiService;
import com.qa.fgj.baymin.net.api.WeatherApi;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by FangGengjia on 2017/2/19.
 */

public class WeatherModel {

    private WeatherApi weatherApi;

    public WeatherModel() {
        weatherApi = RestApiService.getInstance().createApi(WeatherApi.class);
    }

    public Observable<WeatherRoot> getWeatherData(){
        return weatherApi.getWeatherData()
                .map(new Func1<BayMinResponse<WeatherRoot>, WeatherRoot>() {
                    @Override
                    public WeatherRoot call(BayMinResponse<WeatherRoot> bayMinResponse) {
                        if (bayMinResponse.isSucceed()){
                            return bayMinResponse.getContent();
                        }
                        return null;
                    }
                });
    }

}
