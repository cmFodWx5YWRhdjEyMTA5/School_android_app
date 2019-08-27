package com.microbotic.temperature.sdk.tts.core;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import androidx.annotation.NonNull;

import com.microbotic.temperature.sdk.tts.inner.SpeechImpl;
import com.microbotic.temperature.sdk.tts.listener.CsjTTSInitListener;
import com.microbotic.temperature.sdk.tts.listener.OnSpeakListener;

import java.util.HashMap;
import java.util.Locale;

/**
 * Copyright (c) 2018, SuZhou CsjBot. All Rights Reserved.
 * www.csjbot.com
 * <p>
 * Created by 浦耀宗 at 2018/03/02 0002-09:10.
 * Email: puyz@csjbot.com
 */

public class GoogleSpeechImpl extends SpeechImpl {
    private TextToSpeech mTts;

    public GoogleSpeechImpl(Context context, @NonNull final CsjTTSInitListener listener) {
        super(context, listener);
        mTts = new TextToSpeech(mContext.get(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == 0) {
                    isInit = true;
                }

                listener.onInit(status);
            }
        });
    }

    @Override
    public int setLanguage(Locale lang) {
        int ret;
        ret = mTts.setLanguage(lang);
        if (ret == TextToSpeech.LANG_NOT_SUPPORTED) {
            mTts.setLanguage(Locale.US);
        }
//        Log.e("TAG", String.valueOf(ret));

        return ret;
    }

    /**
     * 开始说话
     *
     * @param text
     * @param listener
     */
    @Override
    public int startSpeaking(String text, final OnSpeakListener listener) {
        HashMap<String, String> myHashAlarm = null;

        if (listener != null) {
            myHashAlarm = new HashMap<>();
            myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, String.valueOf(System.currentTimeMillis()));
            mTts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String s) {
                    listener.onSpeakBegin();
                }

                @Override
                public void onDone(String s) {
                    listener.onCompleted();
                }

                @Override
                public void onError(String s) {
                }
            });
        }

        mTts.speak(text, TextToSpeech.QUEUE_FLUSH, myHashAlarm);

        return 0;
    }

    /**
     * 停止说话
     */
    @Override
    public int stopSpeaking() {
        return mTts.stop();
    }


    /**
     * 是否正在说话
     */
    @Override
    public boolean isSpeaking() {
        return mTts.isSpeaking();
    }
}
