package com.qa.fgj.baymin.util;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.util.Log;

import com.qa.fgj.baymin.app.Constant;
import com.qa.fgj.baymin.model.entity.MusicInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 *
 * Created by FangGengjia on 2016/3/22
 * 音乐管理监听类
 */
public class MusicManager implements MediaPlayer.OnCompletionListener {
    private Context context;
    private List<MusicInfo> musicList;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    int listCount;
    int firstPlay;
    int currentPlay;
    private boolean isPause = false;
    public boolean isStart = false;
    Intent intent = new Intent(Constant.MUSIC_STATE_ACTION);
    public MusicManager(final Context context){
        this.context = context;
        getMusicInfos(context)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<MusicInfo>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.shortShow("音乐播放器启动失败");
                    }

                    @Override
                    public void onNext(List<MusicInfo> musicInfos) {
                        listCount = musicList.size();
                        if (listCount != 0){
                            firstPlay = (int) (Math.random() * listCount);
                            currentPlay = firstPlay;
                            initMediaPlayer(currentPlay);
                            mediaPlayer.setOnCompletionListener(MusicManager.this);
                        } else {
                            ToastUtil.shortShow("本地不存在音乐");
                        }
                    }
                });
    }

    private int initMediaPlayer(int playNumber){
        if (playNumber<0){
            playNumber = listCount-1;
        }else if (playNumber>listCount-1){
            playNumber = 0;
        }
        try {
            mediaPlayer.setDataSource(musicList.get(playNumber).getUrl());
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return playNumber;
    }

    public String getTitle(){
        return musicList.get(currentPlay).getTitle();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
//        nextSong();
        mSendBroadCast(Constant.MUSIC_IS_COMPLETED);
    }

    public String start(){
        if (!mediaPlayer.isPlaying()){
            mediaPlayer.start();
            mSendBroadCast(Constant.MUSIC_START);
            isStart = true;
            isPause = false;
        }
       return musicList.get(currentPlay).getTitle();
    }

    public String pause(){
        if (!isStart){
            return "null";
        }else if (mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            mSendBroadCast(Constant.MUSIC_PAUSE);
            isPause = true;
        }
        return musicList.get(currentPlay).getTitle();
    }

    public String continues(){
        if (isPause){
            mediaPlayer.start();
            mSendBroadCast(Constant.MUSIC_CONTINUE);
            isPause = false;
            return musicList.get(currentPlay).getTitle();
        }else {
            return "null";
        }
    }

    public String stop(){
        if (isStart){
            isStart = false;
            mediaPlayer.reset();
            initMediaPlayer(currentPlay);
            mSendBroadCast(Constant.MUSIC_STOP);
            return musicList.get(currentPlay).getTitle();
        }else {
            return "null";
        }
    }

    public String previousSong(){
        if (isStart){
            mediaPlayer.reset();
            currentPlay = currentPlay - 1;
            currentPlay = initMediaPlayer(currentPlay);
            mediaPlayer.start();
            return musicList.get(currentPlay).getTitle();
        }else {
            return "null";
        }
    }

    public String nextSong(){
        if (isStart){
            mediaPlayer.reset();
            currentPlay = currentPlay + 1;
            currentPlay = initMediaPlayer(currentPlay);
            mediaPlayer.start();
            return musicList.get(currentPlay).getTitle();
        }else {
            return "null";
        }
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    private void mSendBroadCast(String msg){
        intent.putExtra("MUSIC_MSG", msg);
        context.sendBroadcast(intent);
    }

    public static Observable<List<MusicInfo>> getMusicInfos (Context context){
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        List<MusicInfo> musicList = new ArrayList<>();
        if (cursor != null){
            for (int i = 0; i < cursor.getCount(); i++){
                cursor.moveToNext();
                MusicInfo musicInfo = new MusicInfo();
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                int isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));
                if (isMusic!=0){
                    musicInfo.setTitle(title);
                    musicInfo.setUrl(url);
                    musicList.add(musicInfo);
                }
            }
            cursor.close();
        }else {
            Log.d("cursor","----------------------------本地音乐为空----------------------------");
            ToastUtil.shortShow("本地音乐为空, 无法播放音乐");
        }
        return Observable.just(musicList);
    }

    public void destroyMediaPlayer(){
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
