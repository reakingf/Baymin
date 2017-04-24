package com.qa.fgj.baymin.net.api;

import com.qa.fgj.baymin.model.entity.AppUpdateInfo;
import com.qa.fgj.baymin.model.entity.BayMinResponse;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import rx.Observable;

/**
 * Created by FangGengjia on 2017/4/22.
 */

public interface AppUpdateApi {

    @GET("/BayMinServlet/checkUpdate")
    Observable<BayMinResponse<AppUpdateInfo>> getUpdateInfo();

    @Streaming//用于下载大文件，意味着直接传递字节码，不需要全部读入内存
    @GET("/BayMinServlet/checkUpdate")
    Observable<ResponseBody> downloadNewVersion(@Query("download") String download);

}
