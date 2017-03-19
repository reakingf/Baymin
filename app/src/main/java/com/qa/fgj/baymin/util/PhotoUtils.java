package com.qa.fgj.baymin.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import com.qa.fgj.baymin.R;
import com.qa.fgj.baymin.app.Constant;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import rx.Observable;

/**
 * 头像处理工具类
 * Created by FangGengjia on 2017/3/17.
 */

public class PhotoUtils {

    private Activity activity;
    private String imagePath;

    /** 设置头像标记：从相册中选择、拍照、裁剪 **/
    public static final int FROM_ALBUM = 0;
    public static final int TAKE_PHOTO = 1;
    public static final int CROP_PICTURE = 2;

    public PhotoUtils(Activity activity) {
        this.activity = activity;
    }

    public void openAlbum(){
        Intent openAlbum = new Intent(Intent.ACTION_PICK);
        //Intent openAlbum = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        //Intent openAlbum = new Intent(Intent.ACTION_GET_CONTENT);
        openAlbum.setType("image/*");
        if (openAlbum.resolveActivity(activity.getPackageManager()) != null){
            activity.startActivityForResult(openAlbum, FROM_ALBUM);
        } else {
            ToastUtil.show(activity.getString(R.string.no_album));
        }
    }

    public Uri openCamera(){
        Uri imgUri = null;
        Intent openCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (openCamera.resolveActivity(activity.getPackageManager()) != null){
            File outputImg = new File(Constant.PATH_IMAGE, String.valueOf(System.currentTimeMillis()));
            imgUri = Uri.fromFile(outputImg);
            //由于这里指定了输出路径，导致在onActivityResult中的intent参数为null，若不指定则会有默认路径
            openCamera.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
            activity.startActivityForResult(openCamera, TAKE_PHOTO);
        }else {
            ToastUtil.show(activity.getString(R.string.no_camera));
        }
        return imgUri;
    }

    public Observable<String> handleAlbumPicture(Uri uri){
        //判断手机系统版本号
        if (Build.VERSION.SDK_INT >= 19) {
            //4.4及以上
            return handleImageOnKitKat(uri);
        } else {
            return handleImageBeforeKitKat(uri);
        }
    }

    /**
     * 裁剪图片
     */
    public void cropPicture(Uri uri){
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        //设置裁剪比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        //intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        //由于这里指定了输出路径，导致在onActivityResult中的intent参数为null
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        activity.startActivityForResult(intent, CROP_PICTURE);
    }

    @TargetApi(19)
    private Observable<String> handleImageOnKitKat(Uri uri){
        String imagePath = null;
        if (DocumentsContract.isDocumentUri(activity, uri)) {
            //如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://" +
                        "downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if (ContentResolver.SCHEME_CONTENT.equalsIgnoreCase(uri.getScheme())) {
            //如果是content类型，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if (ContentResolver.SCHEME_FILE.equals(uri.getScheme())) {
            // 如果是file类型, 则直接获取
            imagePath = uri.getPath();
        } else {
            imagePath = getImagePath(uri, null);
        }
        return Observable.just(imagePath);
    }

    /**
     * API19以下的手机处理方法
     */
    private Observable<String> handleImageBeforeKitKat(Uri uri) {
        String imagePath = getImagePath(uri,null);
        return Observable.just(imagePath);
    }

    /**
     * 处理裁剪后的图片
     */
    public Observable<String> handleCropPicture(Uri imgUri){
        Bitmap photo = null;
        String imgPath = null;
        try {
            photo = BitmapFactory.decodeStream(activity.getContentResolver().openInputStream(imgUri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (photo != null) {
            String imgName = MD5Util.getMD5Digest(String.valueOf(System.currentTimeMillis()));
            imgPath = savePhoto(photo, Environment.getExternalStorageDirectory()
                    .getAbsolutePath()+ "/" + activity.getString(R.string.app_name) + "/image", imgName);
        }
        return Observable.just(imgPath);
    }

    public String getImagePath(Uri uri, String selection) {
        if (uri == null) {
            return null;
        }
        String path = null;
        String[] filePathColumn = {MediaStore.MediaColumns.DATA};
        //通过Uri和selection来获取真实的图片路径
        Cursor cursor = activity.getContentResolver().query(uri, filePathColumn, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    /**
     * 将头像保存到SD卡中
     */
    public String savePhoto(Bitmap photoBitmap, String path,
                            String photoName) {
        String localPath = null;
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            File file = new File(path);
            if (!file.exists()){
                file.mkdir();
            }
            File photoFile = new File(file, photoName);
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(photoFile);
                if (photoBitmap != null) {
                    if (photoBitmap.compress(Bitmap.CompressFormat.PNG, 100,
                            fileOutputStream)) { // 转换完成
                        localPath = photoFile.getPath();
                        fileOutputStream.flush();
                    }
                }
            } catch (IOException e) {
                photoFile.delete();
                localPath = null;
                ToastUtil.show("图片保存失败");
                e.printStackTrace();
            } finally {
                try {
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return localPath;
    }

    /**
     * 转换图片处理成圆形图片
     */
    public Bitmap toRoundBitmap(Bitmap bitmap, Uri tempUri) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float roundPx;
        float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
        if (width <= height) {
            roundPx = width / 2;
            left = 0;
            top = 0;
            right = width;
            bottom = width;
            height = width;
            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        } else {
            roundPx = height / 2;
            float clip = (width - height) / 2;
            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;
            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }

        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect src = new Rect((int) left, (int) top, (int) right,
                (int) bottom);
        final Rect dst = new Rect((int) dst_left, (int) dst_top,
                (int) dst_right, (int) dst_bottom);
        final RectF rectF = new RectF(dst);

        paint.setAntiAlias(true);// 设置画笔无锯齿

        canvas.drawARGB(0, 0, 0, 0); // 填充整个Canvas
        paint.setColor(color);

        /// 以下有两种方法画圆,drawRounRect和drawCircle
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);//
        // 画圆角矩形，第一个参数为图形显示区域，第二个参数和第三个参数分别是水平圆角半径和垂直圆角半径。
        canvas.drawCircle(roundPx, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));// 设置两张图片相交时的模式,参考http://trylovecatch.iteye.com/blog/1189452
        canvas.drawBitmap(bitmap, src, dst, paint); // 以Mode.SRC_IN模式合并bitmap和已经draw了的Circle
        return output;
    }

    /* 上传文件至Server的方法 */
//    private void uploadFile(String fileName) {
//        String end = "\r\n";
//        String twoHyphens = "--";
//        String boundary = "*****";
//        try {
//            URL url = new URL(Constants.UPLOAD_FILE_URL);
//            HttpURLConnection con = (HttpURLConnection) url.openConnection();
//            con.setDoInput(true);
//            con.setDoOutput(true);
//            con.setUseCaches(false);//不缓存
//            con.setRequestMethod("POST");
//
//			/* setRequestProperty */
//            con.setRequestProperty("Connection", "Keep-Alive");
//            con.setRequestProperty("Charset", "UTF-8");
//            con.setRequestProperty("Content-Type",
//                    "multipart/form-data;boundary=" + boundary);
//            //heading为服务器接收的键
//            DataOutputStream ds = new DataOutputStream(con.getOutputStream());
////            ds.writeBytes(twoHyphens + boundary + end);
////            ds.writeBytes("Content-Disposition: form-data; "
////                    + "name=\"head_img\";filename=\"" + fileName + "\"" + end);
////            ds.writeBytes(end);
//			/* 取得文件的FileInputStream */
//            FileInputStream fStream = new FileInputStream(imagePath);
//			/* 设置每次写入1024bytes */
//            int bufferSize = 1024;
//            byte[] buffer = new byte[bufferSize];
//            int length;
//			/* 从文件读取数据至缓冲区 */
//            while ((length = fStream.read(buffer)) != -1) {
//				/* 将资料写入DataOutputStream中 */
//                ds.write(buffer, 0, length);
//            }
////            ds.writeBytes(end);
////            ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
//			/* close streams */
//            fStream.close();
//            ds.flush();
//			/* 取得Response内容 */
//            InputStream is = con.getInputStream();
//            int ch;
//            StringBuffer b = new StringBuffer();
//            while ((ch = is.read()) != -1) {
//                b.append((char) ch);
//            }
//			/* 将Response显示于Dialog */
////            showDialog("上传结果" + b.toString().trim());
//			/* 关闭DataOutputStream */
//            ds.close();
//        } catch (Exception e) {
////            showDialog("上传失败" + e);
//        }
//    }

    public static Bitmap getBitmapById(Context context, int id){
        InputStream is = context.getResources().openRawResource(id);
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inSampleSize = 4; //width，hight设为原来的1/4,图片为原来的1/16
        return BitmapFactory.decodeStream(is,null,options);
    }

    /**
     * 获取缩小比例
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth,
                                            int reqHeight) {
        //原始图片的宽和高
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float)height / (float)reqHeight);
            } else {
                inSampleSize = Math.round((float)width / (float)reqWidth);
            }
        }
        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    /**
     * 获取指定大小的bitmap，防止OOM
     */
    public Bitmap getSpecifiedBitmap(String imagePath, int reqWidth, int reqHeight){
        if (imagePath == null){
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(imagePath, options);
    }
}
