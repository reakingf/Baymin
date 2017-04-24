package com.qa.fgj.baymin.model;

import com.qa.fgj.baymin.model.entity.AppUpdateInfo;
import com.qa.fgj.baymin.model.entity.BayMinResponse;
import com.qa.fgj.baymin.net.RestApiService;
import com.qa.fgj.baymin.net.api.AppUpdateApi;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by FangGengjia on 2017/4/22.
 */

public class UpdateModel {
    
    private AppUpdateApi appUpdateApi;

    public UpdateModel() {
        appUpdateApi = RestApiService.getInstance().createApi(AppUpdateApi.class);
    }
    
    public Observable<AppUpdateInfo> getUpdateInfo(){
        return appUpdateApi.getUpdateInfo()
                .map(new Func1<BayMinResponse<AppUpdateInfo>, AppUpdateInfo>() {
                    @Override
                    public AppUpdateInfo call(BayMinResponse<AppUpdateInfo> httpResponse) {
                        if (httpResponse.isSucceed()){
                            return httpResponse.getContent();
                        }
                        return null;
                    }
                });
    }

    public Observable<ResponseBody> downloadNewApp(){
        return appUpdateApi.downloadNewVersion("true");
    }
}
