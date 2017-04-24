package com.qa.fgj.baymin.presenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.qa.fgj.baymin.R;
import com.qa.fgj.baymin.app.Constant;
import com.qa.fgj.baymin.base.IBasePresenter;
import com.qa.fgj.baymin.model.MainModel;
import com.qa.fgj.baymin.model.UpdateModel;
import com.qa.fgj.baymin.model.entity.AppUpdateInfo;
import com.qa.fgj.baymin.model.entity.BayMinResponse;
import com.qa.fgj.baymin.model.entity.MessageBean;
import com.qa.fgj.baymin.ui.view.IMainView;
import com.qa.fgj.baymin.util.Global;
import com.qa.fgj.baymin.util.LogUtil;
import com.qa.fgj.baymin.util.MusicManager;
import com.qa.fgj.baymin.util.SystemUtil;
import com.qa.fgj.baymin.util.TTSManager;
import com.qa.fgj.baymin.widget.LoadingDialog;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by FangGengjia on 2017/2/18.
 */

public class MainPresenter<T extends IMainView> implements IBasePresenter<T> {

    private T view;
    private MainModel mModel;
    private Context context;

    // 语音唤醒事件管理器
    private EventManager mWpEventManager;
    // 用于标识是否启动连续识别
    private boolean isContinueASRStart = false;
    // 用于标识连续识别模式下是否启动语音识别
    private boolean shouldStartRecognition = true;
    private boolean isMusicPlaying = false;

    //语音合成
    private TTSManager ttsUtil;

    private MusicManager musicManager;
    private MainReceiver mainReceiver;

    private Scheduler uiThread;
    private Scheduler backgroundThread;
    private List<Subscription> subscriptionList = new ArrayList<>();
    private Subscription continuousASRSubscription;

    //语音唤醒监听器，唤醒词：小白你好
    EventListener mEventListener = new EventListener() {
        @Override
        public void onEvent(String name, String params, byte[] data, int offset, int length) {
            if ("wp.data".equals(name)) {
                //每次唤醒成功将会回调name==wp.data事件，被激活的唤醒次在params的word字段
                isContinueASRStart = true;
                shouldStartRecognition = true;
                startContinuousASR();
            } else if ("wp.exit".equals(name)) {
                //唤醒已停止
                LogUtil.d("-------唤醒已停止");
            }
        }
    };

    public MainPresenter(Context context, Scheduler uiThread, Scheduler backgroundThread) {
        this.context = context;
        this.uiThread = uiThread;
        this.backgroundThread = backgroundThread;
        this.mModel = new MainModel(this.context);
    }

    @Override
    public void onCreate() {
        ttsUtil = new TTSManager(context);
        musicManager = new MusicManager(context);
        initReceiver();
    }

    @Override
    public void attachView(T view) {
        this.view = view;
    }

    /**
     * 初始化广播接收器
     */
    private void initReceiver() {
        mainReceiver = new MainReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.PHONE_STATE_ACTION);
        intentFilter.addAction(Constant.TTS_STATE_ACTION);
        intentFilter.addAction(Constant.MUSIC_STATE_ACTION);
        context.registerReceiver(mainReceiver, intentFilter);
    }

    public void loadLoginCache(Subscriber<Boolean> subscriber){
        Subscription subscription = mModel.getLoginCache()
                .subscribeOn(backgroundThread)
                .observeOn(uiThread)
                .subscribe(subscriber);
        subscriptionList.add(subscription);
    }

    public void fetchListData(String id, int offset){
        Subscription subscription = mModel.loadData(id, offset)
                .subscribeOn(backgroundThread)
                .observeOn(uiThread)
                .subscribe(new Subscriber<List<MessageBean>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.d(e.getMessage());
                    }

                    @Override
                    public void onNext(List<MessageBean> list) {
                        view.initListViewData(list);
                    }
                });
        subscriptionList.add(subscription);
    }

    public void onRefreshing(String id, int offset, Subscriber<List> subscriber){
        Subscription subscription =  mModel.loadData(id, offset)
                .observeOn(backgroundThread)
                .subscribeOn(uiThread)
                .subscribe(subscriber);
        subscriptionList.add(subscription);
    }

    public void getAnswer(String question, Subscriber<BayMinResponse<String>> subscriber){
        Subscription subscription = mModel.getAnswer(question)
                .subscribeOn(Schedulers.newThread())
                .observeOn(uiThread)
                .subscribe(subscriber);
        subscriptionList.add(subscription);
    }

    public Observable<Boolean> save(MessageBean msg){
        if (Global.isLogin){
            try{
                mModel.saveData(msg);
            } catch (Exception e){
                e.printStackTrace();
                return Observable.just(false);
            }
            return Observable.just(true);
        }
        return Observable.just(false);
    }

    /**
     * 启动语音唤醒
     */
    public void startWakeUp() {
        if (mWpEventManager == null) {
            mWpEventManager = EventManagerFactory.create(context, "wp");
            mWpEventManager.registerListener(mEventListener);
        }
            HashMap params = new HashMap();
            try {
                params.put("kws-file", "assets:///WakeUp.bin");
                mWpEventManager.send("wp.start", new JSONObject(params).toString(), null, 0, 0);
            } catch (Exception e) {
                LogUtil.e("---启动语音唤醒失败： " + e.toString());
                view.showError("---启动语音唤醒失败： " + e.toString());
            }
    }

    /**
     * 设置是否应该启动识别
     */
    public void setShouldStartRecognition(boolean shouldStart){
        shouldStartRecognition = shouldStart;
    }

    /**
     *设置是否应该启动连续识别
     */
    public void setContinueASRStart(boolean isStart){
        isContinueASRStart = isStart;
    }

    /**
     * 启动连续识别
     */
    public void startContinuousASR() {
        continuousASRSubscription = Observable.interval(0, 3000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        if (isContinueASRStart && !view.isRecognizing() && shouldStartRecognition){
                            startRecognition();
                        }
                    }
                });
        subscriptionList.add(continuousASRSubscription);
    }

    public void startRecognition(){
        if (musicManager != null && musicManager.isPlaying()){
            musicManager.pause();
        }
        
        if (ttsUtil != null){
            ttsUtil.cancel();
        }

        view.startRecognition();
    }

    /**
     * 停止连续识别
     */
    public void stopContinuousASR() {
        setContinueASRStart(false);
        if (continuousASRSubscription != null && !continuousASRSubscription.isUnsubscribed()){
            continuousASRSubscription.unsubscribe();
        }
        //关闭连续识别则启动语音唤醒
        startWakeUp();
    }

    /**
     * 朗读答案
     */
    public void speak(String answer) {
        Intent intent = new Intent(Constant.TTS_STATE_ACTION);
        intent.putExtra("tts_msg", Constant.TTS_STARTING);
        context.sendBroadcast(intent);
        shouldStartRecognition = false;
        if (ttsUtil != null){
            try {
                //TODO 答案过长时如何处理
                if (answer.getBytes("gbk").length > 1024) {
                    String subs = answer.substring(0, 512);
                    ttsUtil.speak(subs);
                } else {
                    ttsUtil.speak(answer);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 停止朗读
     */
    public void stopSpeak(){
        if (ttsUtil != null) {
            ttsUtil.cancel();
        }
    }

    /**
     * 检查答复内容是否是命令指令，目前只检查语音类别指令和音乐播放处理指令
     * @param answer 答复内容
     * @return 检查后结果，若非命令指令则不修改答复内容，若是命令则直接执行命令并修改答复内容
     */
    public String checkCommand(String answer) {
        switch (answer) {
            case "[英文]":
                answer = context.getString(R.string.change_to_English);
//                config.setCurrentLanguage(1);
                break;
            case "[中文]":
                answer = context.getString(R.string.change_to_Chinese);
//                config.setCurrentLanguage(0);
                break;
            case "[音乐播放]":
                if (musicManager == null){
                    answer = context.getString(R.string.request_storage_tip);
//                    permissionUtil.isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE, PermissionUtil.REQUEST_STORAGE_CODE,
//                            context.getString(R.string.request_storage_tip));
                } else {
                    answer = context.getString(R.string.music) + musicManager.start() + context.getString(R.string.playing);
                }
                break;
            case "[音乐暂停]":
                if (musicManager == null){
                    answer = context.getString(R.string.did_not_start_music_manager);
                } else {
                    answer = musicManager.pause();
                    if ("null".equals(answer)) {
                        answer = context.getString(R.string.no_music_play);
                    } else {
                        answer = context.getString(R.string.music) + answer + context.getString(R.string.pause);
                    }
                }
                break;
            case "[音乐继续]":
                if (musicManager == null){
                    answer = context.getString(R.string.did_not_start_music_manager);
                } else {
                    answer = musicManager.continues();
                    if ("null".equals(answer)) {
                        answer = context.getString(R.string.no_music_play);
                    } else
                        answer = context.getString(R.string.music) + answer + context.getString(R.string.continues);
                }
                break;
            case "[音乐停止]":
                if (musicManager == null){
                    answer = context.getString(R.string.did_not_start_music_manager);
                } else {
                    answer = musicManager.stop();
                    if ("null".equals(answer)) {
                        answer = context.getString(R.string.no_music_play);
                    } else {
                        answer = context.getString(R.string.music) + answer + context.getString(R.string.stop);
                    }
                }
                break;
            case "[音乐下一首]":
                if (musicManager == null){
                    answer = context.getString(R.string.did_not_start_music_manager);
                } else {
                    answer = musicManager.nextSong();
                    if ("null".equals(answer)) {
                        answer = context.getString(R.string.music) + musicManager.start() + context.getString(R.string.playing);
                    } else
                        answer = context.getString(R.string.next) + answer;
                }
                break;
            case "[音乐上一首]":
                if (musicManager == null){
                    answer = context.getString(R.string.did_not_start_music_manager);
                } else {
                    answer = musicManager.previousSong();
                    if ("null".equals(answer)) {
                        answer = context.getString(R.string.music) + musicManager.start() + context.getString(R.string.playing);
                    } else
                        answer = context.getString(R.string.previous) + answer;
                }
                break;
        }
        return answer;
    }

    /**
     * 用于处理该Activity的广播接收器
     */
    public class MainReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case Constant.TTS_STATE_ACTION:
                    String ttsState = intent.getStringExtra("tts_msg");
                    if (ttsState.equals(Constant.TTS_STARTING)) {  //语音合成已启动
                        shouldStartRecognition = false;
                    } else if (ttsState.equals(Constant.TTS_STOP) && !isMusicPlaying) {
                        shouldStartRecognition = true;
                    }
                    break;
                case Constant.PHONE_STATE_ACTION: {
                    String musicState = intent.getStringExtra("MUSIC_MSG");
                    String ttsCode = intent.getStringExtra("TTS_MSG");
                    boolean isFirst = intent.getBooleanExtra("isFirst", false);
                    if (!isFirst && musicManager != null) {
                        if (Constant.MUSIC_PAUSE.equals(musicState)){
                            //进入通话，音乐暂停
                            String tip = musicManager.pause();
                            if (!"null".equals(tip)){
                                view.musicStateTip(context.getString(R.string.call_in) + tip
                                    + context.getString(R.string.pause));
                            }
                        } else if (Constant.MUSIC_CONTINUE.equals(musicState)){
                            //通话结束，音乐继续
                            String tip = musicManager.continues();
                            if (!"null".equals(tip)){
                                view.musicStateTip(context.getString(R.string.call_finish) + tip
                                    + context.getString(R.string.continues));
                            }
                        }
                    }
                    if (ttsCode != null && Constant.TTS_STOP.equals(ttsCode)) {
                        ttsUtil.cancel();
                    }
                    break;
                }
                default: {
                    String musicState = intent.getStringExtra("MUSIC_MSG");
                    switch (musicState) {
                        case Constant.MUSIC_START:
                        case Constant.MUSIC_CONTINUE:
                            isMusicPlaying = true;
                            break;
                        case Constant.MUSIC_IS_COMPLETED:
                            isMusicPlaying = false;
                            view.musicStateTip(context.getString(R.string.music) + musicManager.getTitle()
                                    + context.getString(R.string.isCompleted));
                            break;
                        default:
                            isMusicPlaying = false;
                            break;
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void detachView() {
        view = null;
    }

    @Override
    public void onDestroy() {
        if (subscriptionList != null && subscriptionList.size() > 0){
            for (Subscription subscription : subscriptionList) {
                if (subscription != null && !subscription.isUnsubscribed()){
                    subscription.unsubscribe();
                }
            }
        }
        if (mWpEventManager != null){
            mWpEventManager.unregisterListener(mEventListener);
            mWpEventManager = null;
        }
        if (ttsUtil != null){
            ttsUtil.release();
        }
        musicManager.destroyMediaPlayer();
        context.unregisterReceiver(mainReceiver);
    }

}
