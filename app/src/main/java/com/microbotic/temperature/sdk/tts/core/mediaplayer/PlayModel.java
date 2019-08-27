package com.microbotic.temperature.sdk.tts.core.mediaplayer;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Copyright (c) 2018, SuZhou CsjBot. All Rights Reserved.
 * www.csjbot.com
 * <p>
 * Created by 浦耀宗 at 2018/03/09 0009-12:37.
 * Email: puyz@csjbot.com
 */

public class PlayModel {
    enum PlayModelType {
        TYPE_FILE, TYPE_RESOURCE, TYPE_ASSETS
    }

    private FileDescriptor fileDescriptor = null;
    private int offsetStart;
    private long length = Long.MAX_VALUE;

    private PlayModel(Context context, int resId, PlayModelType type) {
        if (type == PlayModelType.TYPE_RESOURCE) {
            AssetFileDescriptor assetFileDescriptor = context.getResources().openRawResourceFd(resId);
            fileDescriptor = assetFileDescriptor.getFileDescriptor();
            length = assetFileDescriptor.getLength();
            offsetStart = 0;
        }
    }

    private PlayModel(Context context, File file, PlayModelType type) {
        if (type == PlayModelType.TYPE_FILE) {
            try {
                fileDescriptor = new FileInputStream(file).getFD();
                length = file.length();
                offsetStart = 0;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private PlayModel(Context context, String path, PlayModelType type) {
        if (type == PlayModelType.TYPE_ASSETS) {
            AssetFileDescriptor assetFileDescriptor = null;
            try {
                assetFileDescriptor = context.getAssets().openFd(path);
                fileDescriptor = assetFileDescriptor.getFileDescriptor();
                length = assetFileDescriptor.getLength();
                offsetStart = 0;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @NonNull
    public static PlayModel ofFilePath(String path) {
        return ofFile(new File(path));
    }

    @NonNull
    public static PlayModel ofFile(File file) {
        return new PlayModel(null, file, PlayModelType.TYPE_FILE);
    }

    @NonNull
    public static PlayModel ofResource(Context context, int resId) {
        return new PlayModel(context, resId, PlayModelType.TYPE_RESOURCE);
    }

    @NonNull
    public static PlayModel ofAssets(Context context, String path) {
        return new PlayModel(context, path, PlayModelType.TYPE_ASSETS);
    }

    public FileDescriptor getFileDescriptor() {
        return fileDescriptor;
    }

    public int getOffsetStart() {
        return offsetStart;
    }

    public long getLength() {
        return length;
    }
}
