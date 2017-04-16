package com.qa.fgj.baymin.widget;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.speech.VoiceRecognitionService;
import com.qa.fgj.baymin.R;
import com.qa.fgj.baymin.app.Constant;
import com.qa.fgj.baymin.ui.activity.MainActivity;
import com.qa.fgj.baymin.util.LogUtil;

import java.util.ArrayList;

import rx.Observable;

/**
 * 语音识别对话框
 * Created by FangGengjia on 2017/2/17.
 */

public class SpeechRecognizeDialog extends Dialog {

    private MainActivity mainActivity;

    private TextView tv_speechContent;
    private ImageView iv_volumeView;
    private Button bt_finishButton;
    private String speechContent;

    /** 用于处理语音识别对话框的音量显示视图控制 **/
    private Handler dialogHandler;
    /** 音量调用延迟时间 */
    static final int DELAY_TIME = 200;
    // Android语音识别器
    private SpeechRecognizer speechRecognizer;
    // 是否已初始化语音识别器
    private boolean isInitRecognizer = false;
    // 语音识别监听器
    MyRecognitionListener recognitionListener;
    private String currentLanguage = Constant.LANGUAGE_CHINESE;//默认为中文输入
    public static boolean isRecognizing = false;

    public SpeechRecognizeDialog(Context context) {
        super(context);
        mainActivity = (MainActivity) context;
    }

    public SpeechRecognizeDialog(Context context, int themeResId) {
        super(context, themeResId);
        mainActivity = (MainActivity) context;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_speech, null);
        setContentView(view);

        tv_speechContent = (TextView) view.findViewById(R.id.speak_tips);
        iv_volumeView = (ImageView) view.findViewById(R.id.iv_volume);
        bt_finishButton = (Button) view.findViewById(R.id.speak_finish);

        recognitionListener = new MyRecognitionListener();
//        WindowManager.LayoutParams lp = getWindow().getAttributes();
//        lp.gravity = Gravity.CENTER;
//        lp.dimAmount = 0.3f;
//        lp.windowAnimations = R.style.Theme_RecognitionDialog;
//        lp.width = Math.round(context.getResources().getDisplayMetrics().widthPixels * 0.6f);
//        getWindow().setAttributes(lp);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        // 创建识别器
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(mainActivity,
                new ComponentName(mainActivity, VoiceRecognitionService.class));
        // 注册监听器
        speechRecognizer.setRecognitionListener(recognitionListener);
        dialogHandler = new Handler();
    }

    protected SpeechRecognizeDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    public void show() {
        super.show();
        startASR();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    // 开始识别
    private void startASR() {
        Intent intent = new Intent();
        bindParams(intent);
        speechRecognizer.startListening(intent);
    }

    private void bindParams(Intent recognizerIntent) {
        // 设置识别参数
        recognizerIntent.putExtra(Constant.EXTRA_SOUND_START, R.raw.bdspeech_recognition_start);
        recognizerIntent.putExtra(Constant.EXTRA_SOUND_SUCCESS, R.raw.bdspeech_recognition_success);
        recognizerIntent.putExtra(Constant.EXTRA_SOUND_END, R.raw.bdspeech_speech_end);
        recognizerIntent.putExtra(Constant.EXTRA_SOUND_ERROR, R.raw.bdspeech_recognition_error);
        recognizerIntent.putExtra(Constant.EXTRA_SOUND_CANCEL, R.raw.bdspeech_recognition_cancel);
    }

    public SpeechRecognizeDialog setText(String text){
        if (!TextUtils.isEmpty(text)) {
            tv_speechContent.setText(text);
        }
        return this;
    }

    public SpeechRecognizeDialog setText(int resId){
        tv_speechContent.setText(resId);
        return this;
    }

    public SpeechRecognizeDialog setImageResource(Drawable drawable){
        iv_volumeView.setImageDrawable(drawable);
        return this;
    }

    public SpeechRecognizeDialog setImageResource(int resId){
        iv_volumeView.setImageResource(resId);
        return this;
    }

    public SpeechRecognizeDialog setOnClickListener(View.OnClickListener listener){
        bt_finishButton.setOnClickListener(listener);
        return this;
    }

    public Observable<String> getSpeechContent(){
        return Observable.just(speechContent);
    }

    public void setSpeechContent(String speechContent) {
        this.speechContent = speechContent;
    }


    public void setCurrentLanguage(int index){
        if (index == 1){
            currentLanguage = Constant.LANGUAGE_ENGLISH;
        }else {
            currentLanguage = Constant.LANGUAGE_CHINESE;
        }
    }

    public String getCurrentLanguage(){
        return currentLanguage;
    }

    /**
     * 语音识别对话框音量显示任务
     */
    Runnable dialogVolumeRunnable = new Runnable() {
        int i = 0;
        @Override
        public void run() {
            if (isRecognizing) {
                if (i < 7) {
                    i++;
                } else {
                    i = 1;
                }
                if (SpeechRecognizeDialog.this.isShowing()) {
                    int resId = getContext().getResources().getIdentifier("v" + i, "drawable", getContext().getPackageName());
                    iv_volumeView.setImageResource(resId);
                }
            } else {
                int resId = getContext().getResources().getIdentifier("v" + 1, "drawable", getContext().getPackageName());
                iv_volumeView.setImageResource(resId);
            }
            dialogHandler.removeCallbacks(dialogVolumeRunnable);
            dialogHandler.postDelayed(dialogVolumeRunnable, DELAY_TIME);
        }
    };

    //识别完成后的activity回调
    public void onActivityResult(Intent data) {
        recognitionListener.onResults(data.getExtras());
    }

    public void stopListening() {
        if (speechRecognizer != null) {
            speechRecognizer.stopListening();
        }
        if (SpeechRecognizeDialog.this.isShowing()){
            SpeechRecognizeDialog.this.dismiss();
        }
    }

    public void onDestroy(){
        if (SpeechRecognizeDialog.this.isShowing()) {
            SpeechRecognizeDialog.this.dismiss();
        }
        if (speechRecognizer != null){
            speechRecognizer.destroy();
            speechRecognizer = null;
        }
        isRecognizing = false;
    }

    /**
     * 自定义语音识别监听器
     */
    private class MyRecognitionListener implements RecognitionListener {

        @Override
        public void onReadyForSpeech(Bundle params) {
            tv_speechContent.setText(R.string.speak_please);
        }

        @Override
        public void onBeginningOfSpeech() {
            tv_speechContent.setText(R.string.listening);
            isRecognizing = true;
            dialogHandler.removeCallbacks(dialogVolumeRunnable);
            dialogHandler.postDelayed(dialogVolumeRunnable, DELAY_TIME);
        }

        @Override
        public void onRmsChanged(float rmsdB) {
            //音量变化，引擎将对每一帧语音回调一次该方法返回音量值，但不能保证该方法会被调用
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
            //获取原始语音，此方法会被回调多次，buffer是当前帧对应的PCM语音数据，拼接后可得到完整的录音数据，但也无法保证该方法一定会被调用
        }

        @Override
        public void onEndOfSpeech() {
            isRecognizing = false;
            dialogHandler.removeCallbacks(dialogVolumeRunnable);
            dialogHandler.postDelayed(dialogVolumeRunnable, DELAY_TIME);
        }

        @Override
        public void onError(int error) {
            //识别出错处理方法，调用该方法将不再调用onResults方法
            dialogHandler.removeCallbacks(dialogVolumeRunnable);
            dialogHandler.postDelayed(dialogVolumeRunnable, DELAY_TIME);
            if (SpeechRecognizeDialog.this.isShowing()) {
                SpeechRecognizeDialog.this.dismiss();
            }
        }

        @Override
        public void onResults(Bundle results) {
            ArrayList<String> rs = results != null
                    ? results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) : null;
            if (rs != null && rs.size() > 0) {
                mainActivity.handleSendQuestion(rs.get(0));
            }
            if (SpeechRecognizeDialog.this.isShowing()){
                SpeechRecognizeDialog.this.dismiss();
            }
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
            ArrayList<String> rs = partialResults != null ? partialResults.getStringArrayList(
                    SpeechRecognizer.RESULTS_RECOGNITION) : null;
            if (rs != null && rs.size() > 0) {
                tv_speechContent.setText(rs.get(0));
            }
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
            //识别事件返回
        }

    }


}
