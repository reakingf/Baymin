package com.qa.fgj.baymin.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.qa.fgj.baymin.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 语音识别对话框
 * Created by FangGengjia on 2017/2/17.
 */

public class SpeechRecognizeDialog extends Dialog {

    @BindView(R.id.speak_tips)
    TextView speechContent;
    @BindView(R.id.iv_volume)
    ImageView volumeView;
    @BindView(R.id.speak_finish)
    Button finshButton;

    public SpeechRecognizeDialog(Context context) {
        super(context);
        setContentView(R.layout.dialog_speech);
        ButterKnife.bind(this);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.gravity = Gravity.CENTER;
        lp.dimAmount = 0.3f;
//        lp.windowAnimations = R.style.dialog_anim_style;
        lp.width = Math.round(context.getResources().getDisplayMetrics().widthPixels * 0.9f);
        getWindow().setAttributes(lp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    public SpeechRecognizeDialog(Context context, int themeResId) {
        super(context, themeResId);
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
        finshButton.setOnClickListener(listener);
        return this;
    }
}
