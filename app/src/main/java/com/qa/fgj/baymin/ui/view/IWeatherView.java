package com.qa.fgj.baymin.ui.view;

import com.qa.fgj.baymin.base.IBaseView;
import com.qa.fgj.baymin.model.entity.weather.WeatherRoot;

/**
 * Created by FangGengjia on 2017/2/19.
 */

public interface IWeatherView extends IBaseView {

    void bindData(WeatherRoot weatherRoot);

}
