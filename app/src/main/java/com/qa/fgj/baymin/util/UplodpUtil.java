//package com.qa.fgj.baymin.util;
//
//import com.qa.fgj.baymin.model.entity.BayMinResponse;
//
//import java.io.File;
//import java.util.List;
//
//import okhttp3.MediaType;
//import okhttp3.MultipartBody;
//import okhttp3.RequestBody;
//import rx.Subscriber;
//
///**
// * 上传文件工具类
// * Created by FangGengjia on 2017/3/24.
// */
//
//public class UplodpUtil {
//
//    /**
//     * 单图上传
//     * @param filePath
//     * 图片地址
//     */
//    public static List<MultipartBody.Part> singleImageUpLoad(String filePath) {
//        File file = new File(filePath);
//        //用户token
//        String token = "ASDDSKKK19990SDDDSS";
//        MultipartBody.Builder builder = new MultipartBody.Builder()
//                .setType(MultipartBody.FORM)
//                .addFormDataPart(ParamKey.TOKEN, token);//ParamKey.TOKEN 自定义参数key常量类，即参数名
//        RequestBody imageBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
//        //imgfile 后台接收图片流的参数名
//        builder.addFormDataPart("imgfile", file.getName(), imageBody);
//        return builder.build().parts();
//    }
//
//    /**
//     * 多图上传
//     * @param pathList
//     * 多张待上传图片的地址列表
//     */
//    public static List<MultipartBody.Part> multiImageUpLoad(List<String> pathList, Subscriber subscriber) {
//        //用户token
//        String token = "ASDDSKKK19990SDDDSS";
//        MultipartBody.Builder builder = new MultipartBody.Builder()
//                .setType(MultipartBody.FORM)//表单类型
//                .addFormDataPart(ParamKey.TOKEN, token);//ParamKey.TOKEN 自定义参数key常量类，即参数名
//        //多张图片
//        for (int i = 0; i < pathList.size(); i++) {
//            File file = new File(pathList.get(i));
//            RequestBody imageBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
//            //"imgfile" + i 后台接收图片流的参数名
//            builder.addFormDataPart("imgfile" + i, file.getName(), imageBody);
//        }
//
//        return builder.build().parts();
//    }
//
//
//}
