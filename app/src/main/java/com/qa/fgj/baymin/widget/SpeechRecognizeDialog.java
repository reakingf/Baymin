package com.qa.fgj.baymin.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.qa.fgj.baymin.R;

/**
 * 语音识别对话框
 * Created by FangGengjia on 2017/2/17.
 */

public class SpeechRecognizeDialog extends Dialog{

    private TextView speechContent;
    private ImageView volumeView;
    private Button finishButton;

    public SpeechRecognizeDialog(Context context) {
        super(context);
    }

    public SpeechRecognizeDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_speech, null);
        setContentView(view);

        speechContent = (TextView) view.findViewById(R.id.speak_tips);
        volumeView = (ImageView) view.findViewById(R.id.iv_volume);
        finishButton = (Button) view.findViewById(R.id.speak_finish);

//        WindowManager.LayoutParams lp = getWindow().getAttributes();
//        lp.gravity = Gravity.CENTER;
//        lp.dimAmount = 0.3f;
//        lp.windowAnimations = R.style.Theme_RecognitionDialog;
//        lp.width = Math.round(context.getResources().getDisplayMetrics().widthPixels * 0.6f);
//        getWindow().setAttributes(lp);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    protected SpeechRecognizeDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public SpeechRecognizeDialog setText(String text){
        if (!TextUtils.isEmpty(text)) {
            speechContent.setText(text);
        }
        return this;
    }

    public SpeechRecognizeDialog setText(int resId){
        speechContent.setText(resId);
        return this;
    }

    public SpeechRecognizeDialog setImageResource(Drawable drawable){
        volumeView.setImageDrawable(drawable);
        return this;
    }

    public SpeechRecognizeDialog setImageResource(int resId){
        volumeView.setImageResource(resId);
        return this;
    }

    public SpeechRecognizeDialog setOnClickListener(View.OnClickListener listener){
        finishButton.setOnClickListener(listener);
        return this;
    }

    /**
     * 伪造开始音量检测
     */
    public void startVolumeDetecting(){
        int i = 0;
        while (true){
            if (i < 7) {
                i++;
            } else {
                i = 1;
            }
            if (this.isShowing()) {
                int resId = getContext().getResources().getIdentifier("v" + i, "drawable", getContext().getPackageName());
                volumeView.setImageResource(resId);
            }
            //0.2s更新一次
            SystemClock.sleep(200);
        }
    }

    /**
     * 停止伪造的音量检测
     */
    public void stopVolumeDetecting(){
        int resId = getContext().getResources().getIdentifier("v" + 1, "drawable", getContext().getPackageName());
        volumeView.setImageResource(resId);
    }
}
