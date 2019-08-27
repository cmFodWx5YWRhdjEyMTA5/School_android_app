package com.microbotic.temperature.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.csjbot.cameraclient.CameraClientAgent;
import com.csjbot.cameraclient.entity.PicturePacket;
import com.csjbot.cameraclient.listener.CameraEventListener;
import com.csjbot.cosclient.CosClientAgent;
import com.csjbot.cosclient.constant.ClientConstant;
import com.csjbot.cosclient.entity.CommonPacket;
import com.csjbot.cosclient.entity.MessagePacket;
import com.csjbot.cosclient.listener.ClientEvent;
import com.csjbot.cosclient.listener.EventListener;
import com.csjbot.cosclient.utils.CosLogger;
import com.csjbot.voiceclient.VoiceClientAgent;
import com.csjbot.voiceclient.constant.VoiceClientConstant;
import com.csjbot.voiceclient.listener.VoiceClientEvent;
import com.csjbot.voiceclient.listener.VoiceEventListener;
import com.csjbot.voiceclient.utils.VoiceLogger;
import com.microbotic.temperature.utils.WeakReferenceHandler;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 负责和底层通信的service
 * Created by jingwc on 2017/8/11.
 */

public class BaseReceiveService extends Service implements EventListener {
    private static final int GET_BATTERY_REQ = 0x001;
    /**
     * 相机数据通信
     */
    private CameraClientAgent cameraClientAgent;

    /**
     * 麦克风整列音频数据
     */
    private VoiceClientAgent voiceClientAgent;


    private static MessageCallBack messageCallBack;
    /**
     * 底层通信对外接口
     */
    private CosClientAgent mainAgent;


    public interface MessageCallBack {
        void jsonMessageComing(String msg);

        void videoDataComing(byte[] videoData);

        void audioDataComing(byte[] audioData);
    }

    /**
     * 设置数据回调
     *
     * @param callBack 数据回调
     */
    public static void setMessageCallBack(MessageCallBack callBack) {
        messageCallBack = callBack;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("TAG", "class:BaseReceiveService method:onCreate");
        createAgents();
        connectAgnets();
    }

    /**
     * setp. 1
     * 初始化各个Agnet
     */
    private void createAgents() {
        mainAgent = CosClientAgent.createRosClientAgent(this, true);
        cameraClientAgent = CameraClientAgent.createRosClientAgent(cameraAgentEeventListener, false);
        voiceClientAgent = VoiceClientAgent.createRosClientAgent(voiceEventListener, true);
    }

    /**
     * setp. 2
     * 让各个客户端都连接到底层Linux
     */
    private void connectAgnets() {
        if (mainAgent != null) {
            mainAgent.connect("192.168.99.101", 60002);
        }

        if (cameraClientAgent != null) {
            cameraClientAgent.connect("192.168.99.101", 60003);
        }

        if (voiceClientAgent != null) {
            voiceClientAgent.connect("192.168.99.101", 60004);
        }
    }

    /**
     * 接受底层消息.从回调 messageCallBack#jsonMessageComing 回到上层
     *
     * @param event 事件
     */
    @Override
    public void onEvent(ClientEvent event) {
        switch (event.eventType) {
            case ClientConstant.EVENT_RECONNECTED:
            case ClientConstant.EVENT_CONNECT_SUCCESS:
                mHandler.sendEmptyMessageDelayed(GET_BATTERY_REQ, 1000);
                sendCosMessage("{\"msg_id\":\"ROBOT_GET_CHARGE_REQ\"}");
                Log.d("TAG", "EVENT_CONNECT_SUCCESS");
                break;
            case ClientConstant.EVENT_CONNECT_FAILD:
                Log.d("TAG", "EVENT_CONNECT_FAILD" + String.valueOf(event.data));
                break;
            case ClientConstant.EVENT_CONNECT_TIME_OUT:
                Log.d("TAG", "EVENT_CONNECT_TIME_OUT  " + String.valueOf(event.data));
                break;
            case ClientConstant.SEND_FAILED:
                Log.d("TAG", "SEND_FAILED");
                break;
            case ClientConstant.EVENT_DISCONNET:
                Log.d("TAG", "EVENT_DISCONNET");
                break;
            case ClientConstant.EVENT_PACKET:
                MessagePacket packet = (MessagePacket) event.data;
                String json = ((CommonPacket) packet).getContentJson();
                CosLogger.warn(json);
                if (messageCallBack != null) {
                    messageCallBack.jsonMessageComing(json);
                }
                dealWithMsg(json);
                break;
            default:
                break;
        }
    }

    int battery;
    int isCharging;

    // 处理通常的消息，如电量等
    private void dealWithMsg(String json) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
            String msg_id = jsonObject.getString("msg_id");

            switch (msg_id) {
                case "ROBOT_GET_BATTERY_RSP":
                    battery = jsonObject.getInt("battery");
                    sendBroadCastToBaidu(battery, isCharging);
                    break;
                case "ROBOT_GET_CHARGE_RSP":
                    isCharging = jsonObject.getInt("charge");
                    sendBroadCastToBaidu(battery, isCharging);
                    break;
                case "ROBOT_CHARGE_STATE_NTF":
//                    int charge_state = jsonObject.getInt("charge_state");
                    sendCosMessage("{\"msg_id\":\"ROBOT_GET_CHARGE_REQ\"}");
                    break;
                case "NAVI_ROBOT_MOVE_TO_NTF":
                    int errorCode = jsonObject.getInt("error_code");
                    if (errorCode == 0) {
                        NavigationService.targetArrived();
                    } else if (errorCode == 20004) {

                    } else if (errorCode == 20007) {
                    }
                    break;
                default:
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    //baidu only
    private void sendBroadCastToBaidu(int battery, int isCharging) {
        Intent intent = new Intent("com.baidu.aip.robot.BatteryReceiver");
        intent.putExtra("electriQuantity", Float.valueOf(battery));
        intent.putExtra("batterStatus", isCharging);
        sendBroadcast(intent);
    }

    /**
     * 相机数据监听，回调到上层
     */
    private CameraEventListener cameraAgentEeventListener = new CameraEventListener() {
        @Override
        public void onCameraEvent(com.csjbot.cameraclient.listener.ClientEvent event) {
            switch (event.eventType) {
                case com.csjbot.cameraclient.constant.ClientConstant.EVENT_PACKET:
                    PicturePacket packet = (PicturePacket) event.data;
                    byte[] bitSrc = packet.getContent();
                    if (messageCallBack != null) {
                        messageCallBack.videoDataComing(bitSrc);
                    }
                    break;
                case com.csjbot.cameraclient.constant.ClientConstant.DUPLICATE_PIC_ERROR:
                    // TODO: 2017/11/16 0016 add pu， 当重复发一个图片5次会报这个错误，说明底下已经不行了
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 声音回调
     */
    private VoiceEventListener voiceEventListener = new VoiceEventListener() {
        @Override
        public void onEvent(VoiceClientEvent event) {
            switch (event.eventType) {

                case VoiceClientConstant.EVENT_RECONNECTED:
                case VoiceClientConstant.EVENT_CONNECT_SUCCESS:
//                connectStatus(ClientConstant.EVENT_CONNECT_SUCCESS);
                    VoiceLogger.info("EVENT_CONNECT_SUCCESS");
                    break;
                case VoiceClientConstant.EVENT_CONNECT_FAILD:
                    VoiceLogger.error("EVENT_CONNECT_FAILD " + String.valueOf(event.data));
                    break;
                case VoiceClientConstant.EVENT_CONNECT_TIME_OUT:
                    VoiceLogger.error("EVENT_CONNECT_TIME_OUT  " + String.valueOf(event.data));
                    break;
                case VoiceClientConstant.SEND_FAILED:
                    VoiceLogger.error("SEND_FAILED");
                    break;
                case VoiceClientConstant.EVENT_DISCONNET:
                    VoiceLogger.error("EVENT_DISCONNET");
                    break;
                case VoiceClientConstant.EVENT_PACKET:
                    //音频数据  16k 16bit 单声道pcm
                    byte[] data = (byte[]) event.data;
                    if (messageCallBack != null) {
                        messageCallBack.audioDataComing(data);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private MyWeakReferenceHandler mHandler = new MyWeakReferenceHandler(this);

    private static class MyWeakReferenceHandler extends WeakReferenceHandler<BaseReceiveService> {

        public MyWeakReferenceHandler(BaseReceiveService reference) {
            super(reference);
        }

        @Override
        protected void handleMessage(BaseReceiveService reference, Message msg) {
            switch (msg.what) {
                case GET_BATTERY_REQ:
                    reference.sendCosMessage("{\"msg_id\":\"ROBOT_GET_BATTERY_REQ\"}");
                    sendEmptyMessageDelayed(GET_BATTERY_REQ, 30000);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 发送消息
     *
     * @param json 文档里的json 消息体
     */
    public void sendCosMessage(String json) {
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mainAgent != null && mainAgent.isConnected()) {
            mainAgent.disConnect();
        }
    }

}
