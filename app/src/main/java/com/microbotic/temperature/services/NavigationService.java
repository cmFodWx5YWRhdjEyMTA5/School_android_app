package com.microbotic.temperature.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import androidx.annotation.Nullable;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.csjbot.cosclient.CosClientAgent;
import com.csjbot.cosclient.entity.CommonPacket;
import com.csjbot.cosclient.entity.MessagePacket;
import com.csjbot.cosclient.utils.CosLogger;
import com.microbotic.temperature.R;
import com.microbotic.temperature.model.RobotPose;
import com.microbotic.temperature.sdk.common.CsjConfig;
import com.microbotic.temperature.sdk.tts.DemoTTS;
import com.microbotic.temperature.sdk.tts.SpeechFactory;
import com.microbotic.temperature.sdk.tts.listener.OnSpeakListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NavigationService extends Service {
    private static SpeechFactory tts = SpeechFactory.getInstance();
    private static String post_not_arrive;
    private static boolean need_speak = CsjConfig.NEED_SPEAK_WHILE_NAVI;

    @Override
    public void onCreate() {
        super.onCreate();

        post_not_arrive = getResources().getString(R.string.post_not_arrive);
    }

    @SuppressLint("HandlerLeak")
    private static Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // if not circle running, return, such as pause state
            if (!isCircleRunning) {
                return;
            }

            switch (msg.what) {
                case POSE_ARRIVED:
                    DemoTTS.getInstance().speak(currentPose.getPoseName() + "到了", new OnSpeakListener() {
                        @Override
                        public void onSpeakBegin() {
                        }

                        @Override
                        public void onCompleted() {
                            // 说完了,过1秒再走
                            currentPoseIndex++;
                            if (currentPoseIndex > poseList.size() - 1) {
                                currentPoseIndex = 0;
                            }

                            currentPose = poseList.get(currentPoseIndex);
                            sendEmptyMessageDelayed(START_NEW_POSE, 1000);
                        }
                    });
                    break;
                case START_NEW_POSE:
                    DemoTTS.getInstance().speak("开始前往" + currentPose.getPoseName(), new OnSpeakListener() {
                        @Override
                        public void onSpeakBegin() {
                        }

                        @Override
                        public void onCompleted() {
                            // 说完了再走
                            sendCosMessage(JSON.toJSONString(currentPose));
                        }
                    });
                    break;
                case TARGET_POSE_NOT_ARRIVE:
                    DemoTTS.getInstance().speak(String.format(Locale.getDefault(), post_not_arrive, currentPose.getPoseName()), new OnSpeakListener() {
                        @Override
                        public void onSpeakBegin() {

                        }

                        @Override
                        public void onCompleted() {
                            currentPoseIndex++;
                            if (currentPoseIndex > poseList.size() - 1) {
                                currentPoseIndex = 0;
                            }

                            currentPose = poseList.get(currentPoseIndex);
                            sendEmptyMessageDelayed(START_NEW_POSE, 1000);
                        }
                    });
                    break;
                default:
                    break;
            }
        }
    };

    private static List<RobotPose> poseList = new ArrayList<>();
    private static boolean isCircleRunning = false;
    private static int currentPoseIndex = -1;
    private static RobotPose currentPose = null;

    private static final int POSE_ARRIVED = 0x10010;
    private static final int START_NEW_POSE = 0x10086;
    private static final int TARGET_POSE_NOT_ARRIVE = 0x10000;

    public static void startCircleRun(List<RobotPose> list) {
        poseList = list;
        isCircleRunning = true;

        currentPoseIndex = 0;
        currentPose = poseList.get(currentPoseIndex);
        DemoTTS.getInstance().speak("开始前往" + currentPose.getPoseName());
        isCircleRunning = true;
        sendCosMessage(JSON.toJSONString(currentPose));
    }

    public static void stopCircleRun() {
        sendCosMessage("{\"msg_id\":\"NAVI_ROBOT_CANCEL_REQ\"}");

        myHandler.removeMessages(POSE_ARRIVED);
        myHandler.removeMessages(START_NEW_POSE);

        poseList.clear();
        currentPoseIndex = -1;
        currentPose = null;
        isCircleRunning = false;
    }


    /**
     * 发送消息
     *
     * @param json 文档里的json 消息体
     */
    public static void sendCosMessage(String json) {
        if (TextUtils.isEmpty(json)) return;
        CosLogger.info(json);

        try {
            MessagePacket packet = new CommonPacket(json.getBytes());
            CosClientAgent.getRosClientAgent().sendMessage(packet);
        } catch (Exception e) {
            CosLogger.error("BaseClientReq:sendReq:e:" + e.toString());
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void targetArrived() {
        myHandler.sendEmptyMessage(POSE_ARRIVED);
    }

    public static void pauseCircleRun() {
        sendCosMessage("{\"msg_id\":\"NAVI_ROBOT_CANCEL_REQ\"}");
        isCircleRunning = false;

        myHandler.removeMessages(POSE_ARRIVED);
        myHandler.removeMessages(START_NEW_POSE);
    }

    public static void resumeCircleRun() {
        if (currentPose != null) {
            DemoTTS.getInstance().speak("开始前往" + currentPose.getPoseName());
            isCircleRunning = true;
            sendCosMessage(JSON.toJSONString(currentPose));
        }
    }
}
