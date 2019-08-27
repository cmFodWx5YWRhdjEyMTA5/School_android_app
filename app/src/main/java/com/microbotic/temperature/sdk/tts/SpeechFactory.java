package com.microbotic.temperature.sdk.tts;

import android.content.Context;

import com.microbotic.temperature.sdk.tts.core.GoogleSpeechImpl;
import com.microbotic.temperature.sdk.tts.core.RecordSpeechImpl;
import com.microbotic.temperature.sdk.tts.inner.SpeechImpl;
import com.microbotic.temperature.sdk.tts.listener.CsjTTSInitListener;
import com.microbotic.temperature.sdk.tts.listener.OnSpeakListener;

import java.util.Locale;

/**
 * Copyright (c) 2018, SuZhou CsjBot. All Rights Reserved.
 * www.csjbot.com
 * <p>
 * Created by 浦耀宗 at 2018/03/02 0002-10:48.
 * Email: puyz@csjbot.com
 */
public class SpeechFactory {
    private static SpeechFactory instance = new SpeechFactory();
    private SpeechImpl speechSpeak;
    private boolean isSpeechInit = false;

    public enum SpeechType {
        SPEECH_TYPE_TTS, SPEECH_TYPE_RECORD
    }

    private SpeechFactory() {
    }

    public static SpeechFactory getInstance() {
        return instance;
    }

    public void init(Context context, SpeechType type, CsjTTSInitListener listener) {
        speechSpeak = null;
        switch (type) {
            case SPEECH_TYPE_RECORD:
                speechSpeak = new RecordSpeechImpl(context, listener);
                break;
            case SPEECH_TYPE_TTS:
            default:
                speechSpeak = new GoogleSpeechImpl(context, listener);
                break;
        }
    }

    public int setLanguage(Locale language) {
        return speechSpeak.setLanguage(language);
    }


    public int startSpeaking(String text, final OnSpeakListener listener) {
        return speechSpeak.startSpeaking(text, listener);
    }

    public void stopSpeaking() {
        speechSpeak.stopSpeaking();
    }
}
