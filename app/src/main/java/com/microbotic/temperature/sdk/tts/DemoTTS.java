package com.microbotic.temperature.sdk.tts;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.csjbot.cosclient.utils.CosLogger;
import com.microbotic.temperature.sdk.common.CsjConfig;
import com.microbotic.temperature.sdk.tts.listener.CsjTTSInitListener;
import com.microbotic.temperature.sdk.tts.listener.OnSpeakListener;

/**
 * Copyright (c) 2019, SuZhou CsjBot. All Rights Reserved.
 * www.csjbot.com
 * <p>
 * Created by 浦耀宗 at 2019/1/10-12:20.
 * Email: puyz@csjbot.com
 */

public class DemoTTS {
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;
    private SpeechFactory tts = null;
    private static boolean isInit = false;

    private DemoTTS() {
        // Step 1
        tts = SpeechFactory.getInstance();

        // Step 2
        tts.init(mContext, SpeechFactory.SpeechType.SPEECH_TYPE_TTS, new CsjTTSInitListener() {
            @Override
            public void onInit(int errorCode) {
                if (errorCode == 0) {
                    isInit = true;
                    CosLogger.debug("TTS debug OK");
                } else {
                    CosLogger.error("TTS init not ok ! error code is " + errorCode);
                }
            }
        });
    }

    private static class DemoTTSInstance {
        @SuppressLint("StaticFieldLeak")
        private static final DemoTTS INSTANCE = new DemoTTS();
    }

    public static DemoTTS createInstance(Context context) {
        mContext = context.getApplicationContext();

        return DemoTTSInstance.INSTANCE;
    }

    public static DemoTTS getInstance() {
        if (isInit) {
            return DemoTTSInstance.INSTANCE;
        } else {
            throw new IllegalStateException("TTS not init");
        }
    }

    public void speak(String text, OnSpeakListener listener) {
        int ret = tts.setLanguage(CsjConfig.MLOCALE);
        CosLogger.debug("speechText res = " + String.valueOf(ret));

        tts.startSpeaking(text, listener);
    }

    public void speak(String text) {
        int ret = tts.setLanguage(CsjConfig.MLOCALE);
        CosLogger.debug("speechText res = " + String.valueOf(ret));

        tts.startSpeaking(text, new OnSpeakListener() {
            @Override
            public void onSpeakBegin() {
                Log.d("startSpeaking", "startSpeaking");
            }

            @Override
            public void onCompleted() {
                Log.d("startSpeaking", "onCompleted");
            }
        });
    }
}
