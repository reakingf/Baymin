//package com.qa.fgj.baymin.ui.service;
//
//import android.app.Service;
//import android.content.Intent;
//import android.database.Cursor;
//import android.media.MediaPlayer;
//import android.os.IBinder;
//import android.provider.MediaStore;
//import android.support.annotation.Nullable;
//import android.util.Log;
//import android.widget.Toast;
//
//import com.qa.fgj.baymin.model.entity.MusicInfo;
//import com.qa.fgj.baymin.util.Global;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by FangGengjia on 2017/4/6.
// */
//
//public class MusicService extends Service implements MediaPlayer.OnCompletionListener {
//
//    private List<MusicInfo> musicList;
//    //媒体播放器对象
//    private MediaPlayer mediaPlayer =  new MediaPlayer();
//    //音乐文件路径
//    private String path;
//    //暂停状态
//    int listCount;
//    int firstPlay;
//    int currentPlay;
//    private boolean isPause = false;
//    public boolean isStart = false;
//
//    @Nullable
//    @Override
//    public IBinder onBind(Intent arg0) {
//        return null;
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//
//        if(mediaPlayer.isPlaying()) {
//            stop();
//        }
//
//        path = intent.getStringExtra("url");
//        int msg = intent.getIntExtra("MSG", 0);
//
//        if(msg == AppConstant.PlayerMsg.PLAY_MSG) {
//            play(0);
//        } else if(msg == AppConstant.PlayerMsg.PAUSE_MSG) {
//            pause();
//        } else if(msg == AppConstant.PlayerMsg.STOP_MSG) {
//            stop();
//        }
//
//        return super.onStartCommand(intent, flags, startId);
//    }
//
//
//    /**
//     * 播放音乐
//     * @param position
//     */
//    private void play(int position) {
//        try {
//            //把各项参数恢复到初始状态
//            mediaPlayer.reset();
//            mediaPlayer.setDataSource(path);
//            //进行缓冲
//            mediaPlayer.prepare();
//            //注册一个监听器
//            mediaPlayer.setOnPreparedListener(new PreparedListener(position));
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 暂停音乐
//     */
//    private void pause() {
//        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
//            mediaPlayer.pause();
//            isPause = true;
//        }
//    }
//
//    /**
//     * 停止音乐
//     */
//    private void stop(){
//        if(mediaPlayer != null) {
//            mediaPlayer.stop();
//            try {
//                // 在调用stop后如果需要再次通过start进行播放,需要之前调用prepare函数
//                mediaPlayer.prepare();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//
//    @Override
//    public void onDestroy() {
//        if(mediaPlayer != null){
//            mediaPlayer.stop();
//            mediaPlayer.release();
//        }
//    }
//
//    @Override
//    public void onCompletion(MediaPlayer mp) {
//
//    }
//
//    /**
//     *
//     * 实现一个OnPrepareLister接口,当音乐准备好的时候开始播放
//     *
//     */
//    private final class PreparedListener implements MediaPlayer.OnPreparedListener {
//
//        private int positon;
//
//        public PreparedListener(int positon) {
//            this.positon = positon;
//        }
//
//        @Override
//        public void onPrepared(MediaPlayer mp) {
//            //开始播放
//            mediaPlayer.start();
//            if(positon > 0) {
//                //如果音乐不是从头播放
//                mediaPlayer.seekTo(positon);
//            }
//        }
//    }
//
//    public List<MusicInfo> getMusicInfos (){
//        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
//                null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
//        List<MusicInfo> musicList = new ArrayList<>();
//        if (cursor != null){
//            for (int i = 0; i < cursor.getCount(); i++){
//                cursor.moveToNext();
//                MusicInfo musicInfo = new MusicInfo();
//                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
//                String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
//                int isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));
//                if (isMusic != 0){
//                    musicInfo.setTitle(title);
//                    musicInfo.setUrl(url);
//                    musicList.add(musicInfo);
//                }
//            }
//            cursor.close();
//        }else {
//            Log.d("cursor","----------------------------本地音乐为空----------------------------");
//            Toast.makeText(Global.appContext, "本地音乐为空, 无法播放音乐", Toast.LENGTH_SHORT).show();
//        }
//        return musicList;
//    }
//
//}