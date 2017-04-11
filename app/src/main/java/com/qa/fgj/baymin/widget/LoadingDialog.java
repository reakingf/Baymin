package com.qa.fgj.baymin.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.qa.fgj.baymin.R;

/**
 * 统一的加载对话框
 * Created by FangGengjia on 2017/4/11.
 */

public class LoadingDialog extends Dialog implements LoadingInterface {

    private final String TAG = LoadingDialog.class.getSimpleName();

    private ImageView loadingImg;
    private TextView mLoadingText;
    private final String defaultTip = "拼命加载中...";

    public LoadingDialog(Context context) {
        super(context, R.style.dialog_style_transparent);
        initDialog();
    }

    /**
     * 初始化对话框
     */
    private void initDialog() {
        setContentView(R.layout.dialog_loading);
        setCancelable(true);
        setCanceledOnTouchOutside(false);
        loadingImg = (ImageView) findViewById(R.id.img_loading);
        mLoadingText = (TextView) findViewById(R.id.txt_loading_tip);
        loadingImg.setImageDrawable(getContext().getResources().getDrawable(R.drawable.anim_loading_circle));
        mLoadingText.setText(defaultTip);
        ((AnimationDrawable) loadingImg.getDrawable()).start();
    }

    @Override
    public void onLoadingData() {
        show();
    }

    @Override
    public void onReloadData() {
        show();
    }

    @Override
    public void onLoadingError() {
        dismiss();
    }

    @Override
    public void onLoadingEmpty() {
        dismiss();
    }

    @Override
    public void onNetworkError() {
        Toast.makeText(getContext(), "网络异常", Toast.LENGTH_LONG).show();
        dismiss();
    }

    @Override
    public void onFirstLoadingToast() {
        dismiss();
    }

    @Override
    public void setEmptyMessageTip(String message) {

    }

    @Override
    public void setErrorMessageTip(String message) {

    }

    @Override
    public void onFinishLoading() {
        dismiss();
    }

    @Override
    public void show() {
        super.show();
    }

    public LoadingDialog setLoadingTip(String message) {
        if (!TextUtils.isEmpty(message)) {
            mLoadingText.setText(message);
        }
        return this;
    }
}
