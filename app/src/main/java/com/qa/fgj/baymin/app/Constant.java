package com.qa.fgj.baymin.app;

import android.os.Environment;

import com.qa.fgj.baymin.R;
import com.qa.fgj.baymin.util.Global;

import java.io.File;

/**
 * 全局常量类
 * Created by FangGengjia on 2017/1/19.
 */

public class Constant {

    //================= PATH ====================

    public static final String PATH_DATA = App.getInstance().getCacheDir()
            .getAbsolutePath() + File.separator + "data";

    public static final String PATH_CACHE = PATH_DATA + "/NetCache";

    public static final String PATH_SDCARD = Environment.getExternalStorageDirectory().getAbsolutePath();

    public static final String PATH_APP = PATH_SDCARD + File.separator + Global.appContext.getString(R.string.app_name);

    public static final String PATH_IMAGE = PATH_APP + "/image";

    public static final String APP_TOKEN = "ASDDSKKK19990SDDDSS";


    //================= 标志音乐播放状态的常量 ====================
    // 标志事件为音乐播放状态的ACTION
    public static final String MUSIC_STATE_ACTION = "com.qa.fgj.baymin.music_state";
    public static final String MUSIC_START = "start";
    public static final String MUSIC_PAUSE = "pause";
    public static final String MUSIC_CONTINUE = "continue";
    public static final String MUSIC_STOP = "stop";
    public static final String MUSIC_IS_COMPLETED = "completed";

    /** 版本更新URL */
    public static final String UPDATE_APP_URL = "http://202.116.195.64:8000/APP/QAChild_APP_update.xml";

}
