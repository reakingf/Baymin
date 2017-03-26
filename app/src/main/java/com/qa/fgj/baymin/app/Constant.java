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

    public static final String PATH_IMAGE = PATH_SDCARD + "/image";

    public static final String APP_TOKEN = "ASDDSKKK19990SDDDSS";

}
