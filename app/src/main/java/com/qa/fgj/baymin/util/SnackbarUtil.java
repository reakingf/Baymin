package com.qa.fgj.baymin.util;

import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * Created by FangGengjia on 2017/2/4
 */
public class SnackbarUtil {

    public static void show(View view, String msg) {
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG).show();
    }

    public static void showShort(View view, String msg) {
        Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).show();
    }

}
