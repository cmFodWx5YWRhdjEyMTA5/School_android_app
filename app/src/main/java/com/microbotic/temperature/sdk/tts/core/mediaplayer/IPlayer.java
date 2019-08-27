package com.microbotic.temperature.sdk.tts.core.mediaplayer;

/**
 * Copyright (c) 2018, SuZhou CsjBot. All Rights Reserved.
 * www.csjbot.com
 * <p>
 * Created by 浦耀宗 at 2018/03/09 0009-09:40.
 * Email: puyz@csjbot.com
 */

public interface IPlayer {
    /**
     * 设置播放监听状态
     *
     * @param listener
     */
    public void setStatusListener(PlayModelStatusListener listener);

    /**
     * 设置播放源
     *
     * @param model
     */
    public void setPlayModel(PlayModel model);

    /**
     * 开始播放
     */
    public void start();

    /**
     * 停止播放
     */
    public void stop();

    /**
     * 暂停播放
     */
    public void pause();

    /**
     * 恢复播放
     */
    public void resume();

    /**
     * reset
     */
    public void reset();

    /**
     * release
     */
    public void release();

    /**
     * 是否播放
     *
     * @return
     */
    public boolean isPlaying();
}
