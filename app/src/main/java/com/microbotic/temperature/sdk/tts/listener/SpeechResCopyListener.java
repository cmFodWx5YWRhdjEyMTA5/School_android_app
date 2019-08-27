package com.microbotic.temperature.sdk.tts.listener;

import java.util.List;

/**
 * Copyright (c) 2018, SuZhou CsjBot. All Rights Reserved.
 * www.csjbot.com
 * <p>
 * Created by 浦耀宗 at 2018/03/08 0008-18:45.
 * Email: puyz@csjbot.com
 */

public interface SpeechResCopyListener {
    void onStart(List<String> fileList);

    void onProgressing(String fileName, int cnt);

    void onCompleted();

    void onError(int errorCode);
}
