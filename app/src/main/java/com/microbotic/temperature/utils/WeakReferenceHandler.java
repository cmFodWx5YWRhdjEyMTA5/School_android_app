package com.microbotic.temperature.utils;


import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * Copyright (c) 2018, SuZhou CsjBot. All Rights Reserved.
 * www.csjbot.com
 * <p>
 * Created by 浦耀宗 at 2018/11/13 0013-17:34.
 * Email: puyz@csjbot.com
 */

public abstract class WeakReferenceHandler<T> extends Handler {
    private WeakReference<T> mReference;

    public WeakReferenceHandler(T reference) {
        mReference = new WeakReference<>(reference);
    }

    @Override
    public void handleMessage(Message msg) {
        if (mReference.get() == null)
            return;
        handleMessage(mReference.get(), msg);
    }

    protected abstract void handleMessage(T reference, Message msg);


}
