package com.qa.fgj.baymin.ui.service;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.qa.fgj.baymin.R;
import com.qa.fgj.baymin.app.Constant;
import com.qa.fgj.baymin.model.UpdateModel;
import com.qa.fgj.baymin.model.entity.AppUpdateInfo;
import com.qa.fgj.baymin.util.LogUtil;
import com.qa.fgj.baymin.util.SystemUtil;
import com.qa.fgj.baymin.util.ToastUtil;
import com.qa.fgj.baymin.widget.LoadingDialog;
import com.qa.fgj.baymin.widget.ShowTipDialog;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

import okhttp3.ResponseBody;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 版本更新
 * Created by FangGengjia on 2017/4/22.
 */

public class AppUpdateManager {
    private Context context;
    private UpdateModel updateModel;
    private Dialog downloadDialog;
    private ProgressBar downloadProgress;
    private static final int BEING_DOWNLOAD = 0;
    private static final int DOWNLOAD_FINISH = 1;
    private long totalLength;
    private boolean isCancelDownload = false;
    private String apkName;

    private UpdateHandler handler;
    private Subscription checkUpdateSubscription;
    private Subscription downloadSubscription;

    private static class UpdateHandler extends Handler {
        WeakReference<AppUpdateManager> mUpdateManager;

        UpdateHandler(AppUpdateManager updateManager) {
            this.mUpdateManager = new WeakReference<>(updateManager);
        }

        public void handleMessage(Message msg) {
            AppUpdateManager updateManager = mUpdateManager.get();
            switch (msg.what) {
                case BEING_DOWNLOAD:
                    updateManager.downloadProgress.setProgress(msg.arg1);
                    break;
                case DOWNLOAD_FINISH:
                    updateManager.downloadDialog.dismiss();
                    updateManager.installApk();
                    break;
            }
        }
    }

    public AppUpdateManager(Context context) {
        this.context = context;
    }

    public void onCreate() {
        updateModel = new UpdateModel();
        handler = new UpdateHandler(this);
        checkVersion();
    }

    private void checkVersion(){
        final LoadingDialog loadingDialog = new LoadingDialog(context);
        loadingDialog.setLoadingTip(context.getString(R.string.detectingNewVersion));
        loadingDialog.show();

        checkUpdateSubscription = updateModel.getUpdateInfo()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<AppUpdateInfo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        loadingDialog.onFinishLoading();
                        ToastUtil.shortShow("检查更新失败");
                    }

                    @Override
                    public void onNext(AppUpdateInfo appUpdateInfo) {
                        loadingDialog.onFinishLoading();
                        String currentVersion = SystemUtil.getCurrentVersion(context);
                        if (currentVersion == null){
                            ToastUtil.shortShow("获取本地版本信息失败");
                        } else if (currentVersion.equals(appUpdateInfo.getVersionCode())){
                            ToastUtil.shortShow(context.getString(R.string.noNewVersion));
                        } else {
                            dealNewVersionApp(appUpdateInfo, currentVersion);
                        }
                    }
                });
    }

    /**
     * 显示存在新版本对话框
     */
    private void dealNewVersionApp(final AppUpdateInfo appUpdateInfo, String currentVersion) {
        final ShowTipDialog showTipDialog = new ShowTipDialog(context);
        showTipDialog.setContentGravity(Gravity.START);
        showTipDialog.setTitleText("检测到新版本V" + appUpdateInfo.getVersionCode() + ", 当前版本为V" + currentVersion);
        showTipDialog.setContentText(appUpdateInfo.getDescription());
        showTipDialog.setPositiveButton(context.getString(R.string.update_now), new ShowTipDialog.onPositiveButtonClick() {
            @Override
            public void onClick() {
                showTipDialog.dismiss();
                downloadNewApp(appUpdateInfo);
            }
        });
        showTipDialog.setNegativeButton(context.getString(R.string.update_next_time), new ShowTipDialog.onNegativeButtonClick() {
            @Override
            public void onClick() {
                showTipDialog.dismiss();
            }
        });
        showTipDialog.show();
    }

    /**
     * 下载新版本的App
     */
    private void downloadNewApp(final AppUpdateInfo appUpdateInfo){
        showDownloadDialog();
        downloadSubscription = updateModel.downloadNewApp()
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe(new Subscriber<ResponseBody>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (downloadDialog != null && downloadDialog.isShowing()){
                            downloadDialog.dismiss();
                        }
                        ToastUtil.shortShow("下载失败");
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        // TODO: 2017/4/24 待处理
                        totalLength = 14552499;
                        saveApk(responseBody, appUpdateInfo);
                    }
                });
    }

    private void showDownloadDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.version_updating);
        builder.setMessage(R.string.being_download);
        final LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.update_progress, null);
        downloadProgress = (ProgressBar) view.findViewById(R.id.update_progress);
        builder.setView(view);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                isCancelDownload = true;
            }
        });
        downloadDialog = builder.create();
        downloadDialog.show();
    }

    private String saveApk(ResponseBody body, AppUpdateInfo appUpdateInfo) {
        String apkPath = null;
        apkName = appUpdateInfo.getName() + ".apk";
        try {
            InputStream in = null;
            BufferedInputStream bis = null;
            FileOutputStream fos = null;
            try {
                in = body.byteStream();
                bis = new BufferedInputStream(in);
                byte[] buffer = new byte[1024];
                apkPath = Constant.PATH_APP + File.separator + apkName;
                File file = new File(apkPath);
                if (file.exists()){
                    file.delete();
                }
                fos = new FileOutputStream(apkPath);
                int count = 0;
                while (!isCancelDownload){
                    int numRead = bis.read(buffer);
                    if (numRead <= 0) {
                        handler.sendEmptyMessage(DOWNLOAD_FINISH);
                        break;
                    }
                    fos.write(buffer, 0, numRead);
                    count += numRead;
                    // 计算进度条位置
                    int currentProgress = (int) (((float) count / totalLength) * 100);
                    // 更新进度
                    Message message = Message.obtain();
                    message.arg1 = currentProgress;
                    message.what = BEING_DOWNLOAD;
                    handler.sendMessage(message);
                }
            } catch (IOException e) {
                LogUtil.d(e.getMessage());
                e.printStackTrace();
            } finally {
                if (bis != null) {
                    bis.close();
                }
                if (in != null) {
                    in.close();
                }
                if (fos != null) {
                    fos.flush();
                    fos.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.d(e.getMessage());
        }
        downloadDialog.dismiss();
        return apkPath;
    }

    private void installApk() {
        File apkFile = new File(Constant.PATH_APP, apkName);
        if (!apkFile.exists()) {
            return;
        }
        // 通过Intent安装APK文件
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkFile.toString()), "application/vnd.android.package-archive");
        context.startActivity(i);
    }

    public void onDestroy() {
        if (checkUpdateSubscription != null && !checkUpdateSubscription.isUnsubscribed()){
            checkUpdateSubscription.unsubscribe();
        }
        if (downloadSubscription != null && !downloadSubscription.isUnsubscribed()){
            downloadSubscription.unsubscribe();
        }
    }
}
