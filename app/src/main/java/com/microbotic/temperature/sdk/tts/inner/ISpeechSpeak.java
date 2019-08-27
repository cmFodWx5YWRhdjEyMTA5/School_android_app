package com.microbotic.temperature.sdk.tts.inner;


import com.microbotic.temperature.sdk.tts.listener.OnSpeakListener;

/**
 * 语音合成提供类接口
 * Created by jingwc on 2017/9/12.
 */

public interface ISpeechSpeak {

    /**
     * 开始说话
     *
     * @param text
     * @param listener
     */
    int startSpeaking(String text, OnSpeakListener listener);

    /**
     * 停止说话
     */
    int stopSpeaking();

    /**
     * 是否正在说话
     */
    boolean isSpeaking();
}
