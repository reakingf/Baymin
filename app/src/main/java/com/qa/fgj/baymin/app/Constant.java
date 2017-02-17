package com.qa.fgj.baymin.app;

import android.content.Context;
import android.os.Environment;

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

    public static final String PATH_SDCARD = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + File.separator + "baymin";




}
