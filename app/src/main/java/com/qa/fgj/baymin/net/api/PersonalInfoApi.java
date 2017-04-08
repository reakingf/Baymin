package com.qa.fgj.baymin.net.api;

import com.qa.fgj.baymin.model.entity.BayMinResponse;
import com.qa.fgj.baymin.model.entity.UserBean;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

/**
 * 用户个人信息相关api
 * Created by FangGengjia on 2017/2/18.
 */

public interface PersonalInfoApi {

    @FormUrlEncoded
    @POST("BayMinServlet/Register")
    Observable<BayMinResponse<UserBean>> register(
            @Field("nickname") String nickname,
            @Field("email") String email,
            @Field("password") String password);

    @FormUrlEncoded
    @POST("BayMinServlet/Login")
    Observable<BayMinResponse<UserBean>> login(
            @Field("email") String email,
            @Field("password") String password);

    //修改密码
    @FormUrlEncoded
    @POST("BayMinServlet/userInfo")
    Observable<BayMinResponse> changePassword(
            @Field("srcPassword") String srcPassword,
            @Field("newPassword") String newPassword
    );

    //更改除密码外的用户信息，包括头像、用户昵称、邮箱、性别
    @Multipart
    @POST("BayMinServlet/upload")
    Observable<BayMinResponse> updateUser(
            @Part MultipartBody.Part partList,
            @Part("nickname") RequestBody nickname,
            @Part("email") RequestBody email,
            @Part("sex") RequestBody sex);

    //下载头像
//    @Streaming//用于下载大文件，意味着直接传递字节码，不需要全部读入内存
    @GET("BayMinServlet/Download")
    Observable<ResponseBody> downLoadAvatar(@Query("email") String email);

}
