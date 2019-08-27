package com.microbotic.temperature.sdk.tts.core;

import android.content.Context;
import androidx.annotation.NonNull;

import com.microbotic.temperature.R;
import com.microbotic.temperature.sdk.tts.core.mediaplayer.CsjMediaPlayer;
import com.microbotic.temperature.sdk.tts.core.mediaplayer.PlayModel;
import com.microbotic.temperature.sdk.tts.inner.SpeechImpl;
import com.microbotic.temperature.sdk.tts.listener.CsjTTSInitListener;
import com.microbotic.temperature.sdk.tts.listener.OnSpeakListener;

import java.util.Locale;

/**
 * Copyright (c) 2018, SuZhou CsjBot. All Rights Reserved.
 * www.csjbot.com
 * <p>
 * Created by 浦耀宗 at 2018/03/08 0008-18:41.
 * Email: puyz@csjbot.com
 */

public class RecordSpeechImpl extends SpeechImpl {
    private CsjMediaPlayer csjMediaPlayer;

    public RecordSpeechImpl(Context context, @NonNull CsjTTSInitListener listener) {
        super(context, listener);
        csjMediaPlayer = new CsjMediaPlayer(context);
    }

    /**
     * 开始说话
     *
     * @param text
     * @param listener
     */
    @Override
    public int startSpeaking(String text, OnSpeakListener listener) {
        csjMediaPlayer.setPlayModel(PlayModel.ofResource(mContext.get(), R.raw.start_working));
        csjMediaPlayer.start();
        return 0;
    }

    /**
     * 停止说话
     */
    @Override
    public int stopSpeaking() {
        return 0;
    }

    /**
     * 是否正在说话
     */
    @Override
    public boolean isSpeaking() {
        return false;
    }

    @Override
    public int setLanguage(Locale lang) {
        return 0;
    }
}
