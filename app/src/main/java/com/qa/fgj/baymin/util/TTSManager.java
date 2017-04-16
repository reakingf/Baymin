package com.qa.fgj.baymin.util;

import android.content.Context;
import android.content.Intent;

import com.baidu.tts.auth.AuthInfo;
import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;
import com.qa.fgj.baymin.R;
import com.qa.fgj.baymin.app.Constant;

import java.util.HashMap;

import static com.baidu.tts.client.SpeechSynthesizer.AUDIO_BITRATE_AMR_15K85;
import static com.baidu.tts.client.SpeechSynthesizer.AUDIO_ENCODE_AMR;
import static com.baidu.tts.client.SpeechSynthesizer.MIX_MODE_DEFAULT;

/**
 * Created by FangGengjia on 2017/4/16.
 * 离在线语音合成管理类
 */

public class TTSManager implements SpeechSynthesizerListener {

    private Context mContext;
    private SpeechSynthesizer speechSynthesizer;
    private HashMap<Integer, String> errorMap = new HashMap<>();

    public TTSManager(Context context){
        mContext = context;
        init();
    }

    private void init(){
        speechSynthesizer = SpeechSynthesizer.getInstance();
        speechSynthesizer.setContext(mContext);
        speechSynthesizer.setSpeechSynthesizerListener(this);
        speechSynthesizer.setApiKey(Constant.MY_API_KEY, Constant.MY_SECRET_KEY);
//        setOfflineParam();
        // 获取语音合成授权信息，如果测试成功可以删除AuthInfo部分的代码
        AuthInfo authInfo = speechSynthesizer.auth(TtsMode.MIX);
        // 判断授权信息是否正确，如果正确则初始化语音合成器并开始语音合成，如果失败则做错误处理
        if (authInfo.isSuccess()) {
            //指定合成模式：TtsMode.ONLINE,TtsMode.OFFLINE,TtsMode.MIX
            speechSynthesizer.initTts(TtsMode.ONLINE);
        } else {
            // 授权失败
            ToastUtil.shortShow("语音合成授权失败");
        }
        setParams();
        initErrorMap();//TODO 调试用
    }

    /**
     * 使用离线引擎时需要设置的参数
     */
    public void setOfflineParam(){
        // 设置离线语音合成授权，需要填入从百度语音官网申请的app_id
        speechSynthesizer.setAppId(Constant.MY_APP_ID);
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, "文本模型文件绝对路径");
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, "声学模型文件绝对路径");
        //如果在百度语音平台的[应用管理]中开通了离线授权，不需要设置该参数，建议将该行代码删除
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_LICENCE_FILE, "离线授权文件绝对路径");
    }

    public void speak(String text){
        //TODO 考虑长于1024个GBK字节的分段重整为包，采用一下方法处理
        // speak(SpeechSynthesizeBag speechSynthesizeBag)：可排队的合成并播放
        // 或 speak(String text, String utteranceId)：utteranceId	合成文本的ID
        // 或 batchSpeak(List<SpeechSynthesizeBag> speechSynthesizeBags)：批量合成并播放
        int ret = speechSynthesizer.speak(text);
        if (ret != 0){
            if (errorMap.containsKey(ret)){
                speechSynthesizer.speak(errorMap.get(ret) + mContext.getString(R.string.cannot_speak));
            } else {
                speechSynthesizer.speak(mContext.getString(R.string.unknow_error));
            }
        }
    }

    public void cancel() {
        if (speechSynthesizer != null){
            speechSynthesizer.stop();
        }
        Intent intent = new Intent(Constant.TTS_STATE_ACTION);
        intent.putExtra("tts_msg", Constant.TTS_STOP);
        mContext.sendBroadcast(intent);
    }

    public void pause(){
        if (speechSynthesizer != null){
            speechSynthesizer.pause();
        }
        Intent intent = new Intent(Constant.TTS_STATE_ACTION);
        intent.putExtra("tts_msg", Constant.TTS_STOP);
        mContext.sendBroadcast(intent);
    }

    public void resume(){
        if (speechSynthesizer != null){
            speechSynthesizer.resume();
            Intent intent = new Intent(Constant.TTS_STATE_ACTION);
            intent.putExtra("tts_msg", Constant.TTS_STARTING);
            mContext.sendBroadcast(intent);
        }
    }

    public void release(){
        if (speechSynthesizer != null){
            speechSynthesizer.release();
            speechSynthesizer = null;
        }
    }

    private void setParams(){
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_VOLUME, "5");//中级音量，范围[0-9]
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEED, "5");//中速，范围[0-9]
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_PITCH, "5");//中调，范围[0-9]
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "0");//0 (普通女声); 1 (普通男声); 2 (特别男声); 3 (情感男声)
        /*
          MIX_MODE_DEFAULT(mix模式下，wifi使用在线合成，非wifi使用离线合成)
          MIX_MODE_HIGH_SPEED_NETWORK(mix模式下，wifi,4G,3G使用在线合成，其他使用离线合成)
          MIX_MODE_HIGH_SPEED_SYNTHESIZE(mix模式下，在线返回速度如果慢（超时，一般为1.2秒）直接切换离线，适用于网络环境较差的情况)
          MIX_MODE_HIGH_SPEED_SYNTHESIZE_WIFI(mix模式下，仅wifi使用在线合成,返回速度如果慢（超时，一般为1.2秒）直接切换离线，适用于仅WIFI网络环境较差的情况)
         */
        speechSynthesizer.setParam(SpeechSynthesizer. PARAM_MIX_MODE, MIX_MODE_DEFAULT);
        /*
          在线合成参数
          AUDIO_ENCODE_BV
          AUDIO_ENCODE_AMR
          AUDIO_ENCODE_OPUS
         */
        speechSynthesizer.setParam(SpeechSynthesizer. PARAM_AUDIO_ENCODE, AUDIO_ENCODE_AMR);
        /*
          在线合成参数
          AUDIO_BITRATE_BV_16K、AUDIO_BITRATE_AMR_6K6、AUDIO_BITRATE_AMR_8K85、AUDIO_BITRATE_AMR_12K65、AUDIO_BITRATE_AMR_14K25、
          AUDIO_BITRATE_AMR_15K85、AUDIO_BITRATE_AMR_18K25、AUDIO_BITRATE_AMR_19K85、AUDIO_BITRATE_AMR_23K05、AUDIO_BITRATE_AMR_23K85、
          AUDIO_BITRATE_OPUS_8K、AUDIO_BITRATE_OPUS_16K、AUDIO_BITRATE_OPUS_18K、AUDIO_BITRATE_OPUS_20K、AUDIO_BITRATE_OPUS_24K、AUDIO_BITRATE_OPUS_32K
         */
        speechSynthesizer.setParam(SpeechSynthesizer. PARAM_AUDIO_RATE, AUDIO_BITRATE_AMR_15K85);
        //合成引擎速度优化等级，取值范围[0, 2]，值越大速度越快（离线引擎）
        speechSynthesizer.setParam(SpeechSynthesizer. PARAM_VOCODER_OPTIM_LEVEL, "0");
    }

    @Override
    public void onSynthesizeStart(String s) {
        // 监听到合成开始，在此添加相关操作
    }

    @Override
    public void onSynthesizeDataArrived(String s, byte[] bytes, int i) {
        // 监听到有合成数据到达，在此添加相关操作
    }

    @Override
    public void onSynthesizeFinish(String s) {
        // 监听到合成结束，在此添加相关操作
    }

    @Override
    public void onSpeechStart(String s) {
        // 监听到合成并播放开始，在此添加相关操作
    }

    @Override
    public void onSpeechProgressChanged(String s, int i) {
        // 监听到播放进度有变化，在此添加相关操作
    }

    @Override
    public void onSpeechFinish(String s) {
        // 监听到播放结束，在此添加相关操作
        Intent intent = new Intent(Constant.TTS_STATE_ACTION);
        intent.putExtra("tts_msg", Constant.TTS_STOP);
        mContext.sendBroadcast(intent);
    }

    @Override
    public void onError(String s, SpeechError speechError) {
    // 监听到出错，在此添加相关操作
    }

    private void initErrorMap(){
        errorMap.put(-1, "在线引擎授权失败");
        errorMap.put(-2, "在线合成请求失败");
        errorMap.put(-5, "在线授权执行时异常");
        errorMap.put(-6, "在线授权时间超时");
        errorMap.put(-8, "在线授权token为空");
        errorMap.put(-9, "在线引擎没有初始化");
        errorMap.put(-100, "离线引擎授权失败");
        errorMap.put(-102, "离线授权下载License失败");
        errorMap.put(-107, "离线授权执行时间超时");
        errorMap.put(-109, "离线引擎未初始化");
        errorMap.put(-112, "离线授权已过期");
        errorMap.put(-113, "离线授权包名不匹配");
        errorMap.put(-114, "离线授权签名不匹配");
        errorMap.put(-115, "离线授权设备信息不匹配");
        errorMap.put(-116, "离线授权平台不匹配");
        errorMap.put(-117, "离线授权的license文件不存在");
        errorMap.put(-200, "混合引擎离线在线都授权失败");
        errorMap.put(-203, "混合引擎授权执行时间超时");
        errorMap.put(-300, "合成文本为空");
        errorMap.put(-301, "合成文本长度过长（不要超过GBK1024个字节）");
        errorMap.put(-400, "TTS未初始化");
        errorMap.put(-401, "TTS模式无效");
        errorMap.put(-402, "TTS合成队列已满（最大限度为1000）");
        errorMap.put(-403, "TTS批量合成文本过多（最多为100）");
        errorMap.put(-405, "TTS APP ID无效");
        errorMap.put(-500, "Context被释放或为空");
        errorMap.put(-600, "播放器为空");
    }
}
