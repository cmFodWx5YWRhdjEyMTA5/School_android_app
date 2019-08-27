package com.microbotic.temperature.sdk.tts.core.mediaplayer;

import android.content.Context;
import android.media.MediaPlayer;

import java.io.IOException;

/**
 * Copyright (c) 2018, SuZhou CsjBot. All Rights Reserved.
 * www.csjbot.com
 * <p>
 * Created by 浦耀宗 at 2018/03/09 0009-13:11.
 * Email: puyz@csjbot.com
 */

public class CsjMediaPlayer implements IPlayer {
    private MediaPlayer mediaPlayer;
    private Context mContext;
    private PlayModelStatusListener playModelStatusListener;

    public CsjMediaPlayer(Context context) {
        mContext = context;

        mediaPlayer = new MediaPlayer();
//        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//        mediaPlayer.prepareAsync();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mediaPlayer.start();
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // 在播放完毕被回调
                if (playModelStatusListener != null) {

                }
            }
        });
    }

    /**
     * 设置播放监听状态
     *
     * @param listener
     */
    @Override
    public void setStatusListener(PlayModelStatusListener listener) {

    }

    /**
     * 设置播放源
     *
     * @param model
     */
    @Override
    public void setPlayModel(PlayModel model) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(model.getFileDescriptor());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 开始播放
     */
    @Override
    public void start() {
        if (mediaPlayer != null) {
//            mediaPlayer.release();
            mediaPlayer.prepareAsync();
        }
    }

    /**
     * 停止播放
     */
    @Override
    public void stop() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    /**
     * 暂停播放
     */
    @Override
    public void pause() {

    }

    /**
     * 恢复播放
     */
    @Override
    public void resume() {

    }

    /**
     * reset
     */
    @Override
    public void reset() {

    }

    /**
     * release
     */
    @Override
    public void release() {

    }

    /**
     * 是否播放
     *
     * @return
     */
    @Override
    public boolean isPlaying() {
        return false;
    }
}
