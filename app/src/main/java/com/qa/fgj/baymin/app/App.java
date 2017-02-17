package com.qa.fgj.baymin.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Process;
import android.support.v7.app.AppCompatDelegate;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.orhanobut.logger.Logger;
import com.qa.fgj.baymin.di.component.AppComponent;
import com.qa.fgj.baymin.di.component.DaggerAppComponent;
import com.qa.fgj.baymin.di.module.AppModule;
import com.qa.fgj.baymin.net.RestApiService;
import com.qa.fgj.baymin.util.Global;
import com.squareup.leakcanary.LeakCanary;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by FangGengjia on 2017/1/19.
 */

public class App extends Application {

    private static App instance;
    private static AppComponent appComponent;
    private Set<Activity> allActivities;

    public static int SCREEN_WIDTH = -1;
    public static int SCREEN_HEIGHT = -1;
    public static float DIMEN_RATE = -1.0F;
    public static int DIMEN_DPI = -1;

    public App() {
        instance = this;
    }

    public static synchronized App getInstance(){
        if (instance == null){
            new App();
        }
        return instance;
    }

    static {
        AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_AUTO);
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        instance = this;
        Global.appContext = getApplicationContext();

        //初始化数据库
        Global.initDB();

        //初始化网络模块
        RestApiService.createInstance(this);

        //获取屏幕宽高
        getScreenSize();

        //初始化日志
        Logger.init(getPackageName()).hideThreadInfo();

        //初始化错误信息收集
//        initBugly();

        //初始化内存泄漏检测
        LeakCanary.install(this);

        //初始化过度绘制检测
//        BlockCanary.install(this, new AppBlockCanaryContext()).start();

        //初始化tbs x5 webview
//        QbSdk.allowThirdPartyAppDownload(true);
//        QbSdk.initX5Environment(getApplicationContext(),
//                QbSdk.WebviewInitType.FIRSTUSE_AND_PRELOAD,
//                new QbSdk.PreInitCallback() {
//            @Override
//            public void onCoreInitFinished() {
//            }
//
//            @Override
//            public void onViewInitFinished(boolean b) {
//            }
//        });

    }

//    protected void attachBaseContext(Context base) {
//        super.attachBaseContext(base);
//        MultiDex.install(this);
//    }

//    private void initBugly() {
//        Context context = getApplicationContext();
//        String packageName = context.getPackageName();
//        String processName = SystemUtil.getProcessName(Process.myPid());
//        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);
//        strategy.setUploadProcess(processName == null || processName.equals(packageName));
//        CrashReport.initCrashReport(context, Constant.BUGLY_ID, isDebug, strategy);
//    }

    public void addActivity(Activity activity){
        if (allActivities == null){
            allActivities = new HashSet<>();
        }
        allActivities.add(activity);
    }

    public void removeActivity(Activity activity){
        if (allActivities != null){
            allActivities.remove(activity);
        }
    }

    public void exitApp(){
        if (allActivities != null){
            synchronized (allActivities){
                for (Activity activity : allActivities) {
                    activity.finish();
                }
            }
        }
        Process.killProcess(Process.myPid());
        System.exit(0);
    }

    private void getScreenSize(){
        WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        Display display = windowManager.getDefaultDisplay();
        display.getMetrics(dm);
        DIMEN_RATE = dm.density / 1.0F;
        DIMEN_DPI = dm.densityDpi;
        SCREEN_WIDTH = dm.widthPixels;
        SCREEN_HEIGHT = dm.heightPixels;
        if (SCREEN_WIDTH > SCREEN_HEIGHT){
            int temp = SCREEN_HEIGHT;
            SCREEN_HEIGHT = SCREEN_WIDTH;
            SCREEN_WIDTH = temp;
        }
    }

    public static AppComponent getAppComponent(){
        if (appComponent == null){
            appComponent = DaggerAppComponent.builder()
                    .appModule(new AppModule(instance)).build();
        }
        return appComponent;
    }

}
