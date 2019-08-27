package com.microbotic.temperature.sdk.tts.inner;

import android.content.Context;
import androidx.annotation.NonNull;

import com.microbotic.temperature.sdk.tts.listener.CsjTTSInitListener;

import java.lang.ref.WeakReference;
import java.util.Locale;

/**
 * Copyright (c) 2018, SuZhou CsjBot. All Rights Reserved.
 * www.csjbot.com
 * <p>
 * Created by 浦耀宗 at 2018/03/08 0008-17:43.
 * Email: puyz@csjbot.com
 */
public abstract class SpeechImpl implements ISpeechSpeak {
    protected WeakReference<Context> mContext;
    protected boolean isInit = false;

    public SpeechImpl(Context context, @NonNull final CsjTTSInitListener listener) {
        mContext = new WeakReference<>(context.getApplicationContext());
    }

    public abstract int setLanguage(Locale lang);

    public boolean isSpeechInit() {
        return isInit;
    }
}
