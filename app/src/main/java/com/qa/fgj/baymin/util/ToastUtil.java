package com.qa.fgj.baymin.util;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.qa.fgj.baymin.R;
import com.qa.fgj.baymin.app.App;
import com.qa.fgj.baymin.app.Constant;

/**
 * Created by FangGengjia on 2017/2/4
 */

public class ToastUtil {

    static ToastUtil toastUtil;
    Context context;
    Toast toast;
    String msg;

    public static void show(int resId){
        show(App.getInstance().getString(resId));
    }

    public static void show(String msg){
        if (toastUtil == null){
            toastUtil = new ToastUtil(Global.appContext);
        }
        toastUtil.setText(msg);
        toastUtil.create().show();
    }

    public static void shortShow(String msg) {
        if (toastUtil == null) {
            toastUtil = new ToastUtil(App.getInstance());
        }
        toastUtil.setText(msg);
        toastUtil.createShort().show();
    }

    public ToastUtil(Context context) {
        this.context = context;
    }

    public Toast create() {
        if (context == null){
            context = Global.appContext;
        }
        View contentView = View.inflate(context, R.layout.dialog_toast, null);
        TextView tv = (TextView) contentView.findViewById(R.id.tv_toast_msg);
        toast = new Toast(context);
        toast.setView(contentView);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        tv.setText(msg);
        return toast;
    }

    public Toast createShort() {
        View contentView = View.inflate(context, R.layout.dialog_toast, null);
        TextView tv = (TextView) contentView.findViewById(R.id.tv_toast_msg);
        toast = new Toast(context);
        toast.setView(contentView);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        tv.setText(msg);
        return toast;
    }

    public void show(){
        if (toast != null){
            toast.show();
        }
    }

    private void setText(String text) {
        msg = text;
    }


}
