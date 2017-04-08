//package com.qa.fgj.baymin.util;
//
//import android.app.AlertDialog;
//import android.app.Dialog;
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.pm.PackageInfo;
//import android.content.pm.PackageManager;
//import android.net.Uri;
//import android.os.Environment;
//import android.os.Handler;
//import android.os.Message;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.ProgressBar;
//import android.widget.Toast;
//
//import com.qa.fgj.baymin.R;
//import com.qa.fgj.baymin.app.Constant;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.lang.ref.WeakReference;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.HashMap;
//
///**
// * Created by FangGengjia on 2016/3/27
// * 版本更新类
// */
//public class UpdateManager {
//    private Context context;
//    private static final int HAS_NEW_VERSION = 0;
//    private static final int NO_NEW_VERSION = 1;
//    private static final int BEING_DOWNLOAD = 2;
//    private static final int DOWNLOAD_FINISH = 3;
//    private HashMap<String, String> newVersionInfo;
//    private String downloadSavePath;
//    private int downloadNumber;
//    private boolean isCancelDownload = false;
//    private ProgressBar progressBar;
//    private Dialog downloadDialog;
//    private String versionName = null;
//    private String newVersion = null;
//    private ProgressDialog checkVersionDialog;
//    private UpdateHandler handler;
//
//    static class UpdateHandler extends Handler {
//        WeakReference<UpdateManager> mUpdateManager;
//
//        public UpdateHandler(UpdateManager updateManager) {
//            this.mUpdateManager = new WeakReference<>(updateManager);
//        }
//
//        public void handleMessage(Message msg) {
//            UpdateManager updateManager = mUpdateManager.get();
//            switch (msg.what) {
//                case HAS_NEW_VERSION:
//                    updateManager.checkVersionDialog.dismiss();
//                    updateManager.showDetectedDialog();
//                    break;
//                case NO_NEW_VERSION:
//                    updateManager.checkVersionDialog.dismiss();
//                    String tips = updateManager.context.getString(R.string.noNewVersion);
//                    Toast.makeText(updateManager.context.getApplicationContext(), tips, Toast.LENGTH_LONG).show();
//                    break;
//                case BEING_DOWNLOAD:
//                    updateManager.progressBar.setProgress(updateManager.downloadNumber);
//                    break;
//                case DOWNLOAD_FINISH:
//                    updateManager.installApk();
//                    break;
//                default:
//                    break;
//            }
//        }
//    };
//
//    public UpdateManager(Context context) {
//        this.context = context;
//        handler = new UpdateHandler(UpdateManager.this);
//        checkVersionDialog = new ProgressDialog(context);
//        String tips = context.getString(R.string.detectingNewVersion);
//        checkVersionDialog.setMessage(tips);
//        checkVersionDialog.setCancelable(true);
//        checkVersionDialog.show();
//    }
//
//    public void isUpdate() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                String versionName = getVersionCode(context);
//                ParseXMLManager parseXMLManager = new ParseXMLManager();
//                String path = Constant.UPDATE_APP_URL;
//                HttpURLConnection connection = null;
//                try {
//                    URL url = new URL(path);
//                    connection = (HttpURLConnection) url.openConnection();
//                    connection.setConnectTimeout(8000);
//                    connection.setReadTimeout(8000);
//                    connection.setRequestMethod("GET");
//                    InputStream inputStream = connection.getInputStream();
//                    newVersionInfo = parseXMLManager.parseXML(inputStream);
//                    Message message = new Message();
//                    if (newVersionInfo != null) {
//                        String serviceName = newVersionInfo.get("version");
//                        if (!serviceName.equals(versionName)) {
//                            message.what = HAS_NEW_VERSION;
//                            newVersion = serviceName;
//                        }else {
//                            message.what = NO_NEW_VERSION;
//                        }
//                    }
//                    handler.sendMessage(message);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }finally {
//                    if (connection!=null){
//                        connection.disconnect();
//                    }
//                }
//            }
//        }).start();
//    }
//
//    private String getVersionCode(Context context) {
//        PackageManager packageManager = context.getPackageManager();
//        PackageInfo packageInfo=null;
//        try {
//            packageInfo= packageManager.getPackageInfo(context.getPackageName(), 0);
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }
//        if (packageInfo.versionName!=null){
//            versionName = packageInfo.versionName;
//        }
//        return versionName;
//    }
//
//    private void showDetectedDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setTitle("检测到新版本" + newVersion + ", 当前版本为" + versionName);
//        String description = newVersionInfo.get("description");
//        builder.setMessage(description);
//        builder.setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//                showDownloadDialog();
//            }
//        });
//        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//        Dialog noticeDialog = builder.create();
//        noticeDialog.show();
//    }
//
//    private void showDownloadDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setTitle(R.string.version_updating);
//        builder.setMessage(R.string.being_download);
//        final LayoutInflater inflater = LayoutInflater.from(context);
//        View view = inflater.inflate(R.layout.update_progress, null);
//        progressBar = (ProgressBar) view.findViewById(R.id.update_progress);
//        builder.setView(view);
//        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//                isCancelDownload = true;
//            }
//        });
//        downloadDialog = builder.create();
//        downloadDialog.show();
//        downloadApk();
//    }
//
//    private void downloadApk() {
//        new downloadApkThread().start();
//    }
//
//    private class downloadApkThread extends Thread {
//        @Override
//        public void run() {
//            try {
//                // 判断SD卡是否存在，并且是否具有读写权限
//                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//                    // 获得存储卡的路径
//                    String sdPath = Environment.getExternalStorageDirectory() + "/";
//                    downloadSavePath = sdPath + "download";
//                    URL url = new URL(newVersionInfo.get("url"));
//                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                    conn.connect();
//                    // 获取文件大小
//                    int length = conn.getContentLength();
//                    InputStream is = conn.getInputStream();
//                    File file = new File(downloadSavePath);
//                    // 判断文件目录是否存在
//                    if (!file.exists()) {
//                        file.mkdir();
//                    }
//                    File apkFile = new File(downloadSavePath, newVersionInfo.get("name"));
//                    FileOutputStream fos = new FileOutputStream(apkFile);
//                    int count = 0;
//                    // 缓存
//                    byte buf[] = new byte[1024];
//                    // 写入到文件中
//                    do {
//                        int numRead = is.read(buf);
//                        count += numRead;
//                        // 计算进度条位置
//                        downloadNumber = (int) (((float) count / length) * 100);
//                        // 更新进度
//                        handler.sendEmptyMessage(BEING_DOWNLOAD);
//                        if (numRead <= 0) {
//                            handler.sendEmptyMessage(DOWNLOAD_FINISH);
//                            break;
//                        }
//                        // 写入文件
//                        fos.write(buf, 0, numRead);
//                    } while (!isCancelDownload);// 点击取消就停止下载.
//                    fos.close();
//                    is.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            downloadDialog.dismiss();
//        }
//    }
//
//    private void installApk() {
//        File apkFile = new File(downloadSavePath, newVersionInfo.get("name"));
//        if (!apkFile.exists()) {
//            return;
//        }
//        // 通过Intent安装APK文件
//        Intent i = new Intent(Intent.ACTION_VIEW);
//        i.setDataAndType(Uri.parse("file://" + apkFile.toString()), "application/vnd.android.package-archive");
//        context.startActivity(i);
//    }
//
//}
