package com.jumper.bluetoothdevicelib.core;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.jumper.bluetoothdevicelib.BuildConfig;
import com.jumper.bluetoothdevicelib.config.DeviceConfig;
import com.jumper.bluetoothdevicelib.device.ring.ByteUtil;
import com.jumper.bluetoothdevicelib.helper.L;
import com.jumper.bluetoothdevicelib.result.Listener;
import com.jumper.bluetoothdevicelib.result.Result;
import com.jumper.bluetoothdevicelib.result.WriteResultListener;
import com.jumper.bluetoothdevicelib.service.BluetoothLeService;

import java.util.ArrayList;


/**
 * Created by Terry on 2016/7/19.
 */
public class ADBlueTooth implements BlueUnit.LeCallBack {


    public static final String VERSION = BuildConfig.VERSION_NAME;

    protected ArrayList<BluetoothDevice> mLeDevices = new ArrayList<BluetoothDevice>();


    /**
     * 不支持BLE蓝牙
     */
    public static final int INIT_FAIL_NOT_SUPPORT_BLE = -2;

    /**
     * 蓝牙未开启
     */
    public static final int INIT_FAIL_NOT_ENABLE = -1;

    /**
     * 初始化成功
     */
    public static final int INIT_SUCCESS = 1;


    private Handler handler = new Handler(Looper.getMainLooper());

    protected BluetoothLeService mBluetoothLeService;

    private Context context;


    protected BluetoothDevice mDevice;

    public BlueUnit mBlueUnit;

    /**
     * 当前正在操作的设备
     */
    private DeviceConfig mDeviceConfig;

    private boolean isFindBoroacastDevice;

    private GattHelper gattHelper;


    private DeviceConfig[] deviceConfigs;


    private BlueToothCallback mBlueToothCallBack;

    Listener mListener;

    WriteResultListener mWriteResultListener;


    public DeviceConfig getDeviceConfig() {
        return mDeviceConfig;
    }

    /**
     * @param context 上下文
     * @param configs 设备参数
     */
    public ADBlueTooth(Context context, DeviceConfig... configs) {
        this.context = context;
        if (configs == null || configs.length == 0) {
            throw new IllegalArgumentException("请确保configs不为空");
        } else if (configs.length == 1) {
            this.mDeviceConfig = configs[0];
        } else {
            this.deviceConfigs = configs;
        }
    }

//    public ADBlueTooth(Context context, DeviceConfig config) {
//
//        this.context = context;
//
//
//
//
//        ADBlueTooth a = new ADBlueTooth(null,new DeviceTemperature());
//        a.setResultListener(new Result.Listener<ADDeviceType>() {
//            @Override
//            public void onResult(ADDeviceType result) {
//
//            }
//
//            @Override
//            public void onConnectedState(int state) {
//
//            }
//        });
//    }


    /**
     * 设置一个结果监听的接口
     *
     * @param listener 监听的接口
     * @param <T>
     */
    public <T> void setResultListener(Listener<T> listener) {
        this.mListener = listener;
    }


    /**
     * 设置 一个写数据成功的回调
     *
     * @param writeResultListener
     */
    public void setWriteResultListener(WriteResultListener writeResultListener) {
        this.mWriteResultListener = writeResultListener;
    }


    /**
     * 初始化一些信息并开始工作
     *
     * @return 初始化结果状态码<br>
     * @see #INIT_FAIL_NOT_SUPPORT_BLE
     * @see #INIT_FAIL_NOT_ENABLE
     * @see #INIT_SUCCESS
     */
    public int init() {
        if (!BlueUnit.isHaveBleFeature(context)) {
            return INIT_FAIL_NOT_SUPPORT_BLE;
        }

        if (!BlueUnit.isEnabled(context)) {
            return INIT_FAIL_NOT_ENABLE;
        }


        // 初始化工具类
        mBlueUnit = new BlueUnit(context, handler, hasBroadcastDevice());
        mBlueUnit.setLeCallBack(this);//设置扫描设备回调
        mBlueToothCallBack = new BlueToothCallback() {
            @Override
            public void onConnected() {
                broadcastDeviceConnect = true;
                if (mListener != null)
                    mListener.onConnectedState(Result.STATE_CONNECTED);
            }

            @Override
            public void onDisconnect() {
                broadcastDeviceConnect = false;
                if (mListener != null)
                    mListener.onConnectedState(Result.STATE_DISCONNECTED);
            }

            @Override
            public void onDataReceived(byte[] data) {
                if (mListener != null) {
                    mListener.onResult(mDeviceConfig.parseData(data, ADBlueTooth.this));
                }
            }

            @Override
            public void onServiceDiscover() {
                gattHelper.doServiceDiscovered(mBluetoothLeService.getSupportedGattServices(), true);
            }

            @Override
            public void onNotifySuccess() {
                if (mListener != null)
                    mListener.onConnectedState(Result.STATE_NOTIFY_SUCCESS);
            }

            @Override
            public void onWriteDataReceived(boolean success) {
                if (mWriteResultListener != null) {
                    mWriteResultListener.onWriteSuccess();
                }
            }
        };
        //设置服务连接回调
        mBlueUnit.setLeServiceCallBack(new BlueUnit.LeServiceCallBack() {
            @Override
            public void onMyServiceConnected(BluetoothLeService service) {
                mBluetoothLeService = service;
                if (mDeviceConfig != null)
                    mBluetoothLeService.setDeviceUUID(mDeviceConfig.notifyUUIDStr);
                mBluetoothLeService.setBluetoothCallBack(mBlueToothCallBack);
            }
        });

        //初始化  特征工具类
        gattHelper = new GattHelper(mBlueUnit);


        L.d("00000000初始化成功。。。");

        startScan();

        return INIT_SUCCESS;
    }


    private boolean hasBroadcastDevice() {
        if (mDeviceConfig != null) {
            return mDeviceConfig.hasBroadcastDevice;
        } else {
            if (deviceConfigs == null) return false;
            for (DeviceConfig dc : deviceConfigs) {
                if (dc.hasBroadcastDevice) {
                    return true;
                }
            }
        }
        return false;
    }


    private void startScan() {
        if (mBlueUnit != null) {
            L.d("-----------start--scan---------->");
            mBlueUnit.startLeScan();
        }
    }


    private void stopScan() {
        if (mBlueUnit != null) {
            L.d("-----------stop--scan---------->");
            mBlueUnit.stopLeScan();
        }
    }


    public void writeData(byte[] datas) {
        if (gattHelper != null && broadcastDeviceConnect) {
            L.e("WriteData--" + ByteUtil.toHexString(datas));
            gattHelper.writeValue(datas);
        }
    }


    private String macString = null;


    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        L.d("--device------->" + device.getName());
        if (TextUtils.isEmpty(device.getName())) return;
        if (hasBroadcastDevice()) {//有两种设备
            if (isBroadcastName(device)) {//找到了广播设备
                if (!mDeviceConfig.isRealData(scanRecord)) {
                    return;
                }

                //过了这个判断就视为广播设备连接上了
                if (!isBroadcastDeviceConnnected() && mDeviceConfig.isConnnectedJudgeByData(scanRecord)) {
                    mBlueToothCallBack.onConnected();
                    if (macString == null)
                        macString = device.getAddress();
                }

                if (macString != null && macString.equals(device.getAddress())) {
                    //不是正确的体重数据
                    if (!mDeviceConfig.isConnnectedJudgeByData(scanRecord)) {
                        //不是正确的体重数据，但是设备已经连接上了，就断开
                        if (isBroadcastDeviceConnnected()) {
                            mBlueToothCallBack.onDisconnect();
                            macString = null;
                            isFindBoroacastDevice = false;
                        }
                    } else {
                        //是正确的体重数据就回调出去
                        mBlueToothCallBack.onDataReceived(scanRecord);
                    }
                }
                isFindBoroacastDevice = true;
            } else if (!isFindBoroacastDevice) {//这个设备是连接设备 且 之前没有发现广播设备
                handleLinkedDevice(device,scanRecord);
            }
        } else {//不存在广播设备
            handleLinkedDevice(device,scanRecord);
        }
    }


    private boolean isBroadcastName(BluetoothDevice device) {
        if (mDeviceConfig == null && deviceConfigs == null) return false;
        if (deviceConfigs != null) {
            for (DeviceConfig deviceConfig : deviceConfigs) {
                if (isSingleBroadcastName(deviceConfig, device)) return true;
            }
        }

        if (mDeviceConfig != null) return isSingleBroadcastName(mDeviceConfig, device);
        return false;
    }


    private boolean isSingleBroadcastName(DeviceConfig deviceConfig, BluetoothDevice device) {
        if (device == null || device.getName() == null) return false;
        for (int i = 0; i < deviceConfig.deviceName.length; i++) {
            //startsWith的判断是兼容广播设备的，因为不知道确切的名字，只知道开头部分
            if (deviceConfig.deviceName[i].equals(device.getName())
                    || device.getName().startsWith(deviceConfig.deviceName[i])) {
                mDeviceConfig = deviceConfig;
                return true;
            }
        }
        return false;
    }


    private void handleLinkedDevice(BluetoothDevice device,byte[] scanRecord) {
        L.e("不是广播式设备");
        boolean result = false;
        if (!mLeDevices.contains(device)) {
            mLeDevices.add(device);
            L.d("device-->" + device.getName());
            if (device.getName() == null) return;
            if (mDeviceConfig != null) {
                result = handleSingleDevice(mDeviceConfig, device,scanRecord);
            } else if (deviceConfigs != null) {
                for (DeviceConfig deviceConfig : deviceConfigs) {
                    if (handleSingleDevice(deviceConfig, device,scanRecord)) {
                        mDeviceConfig = deviceConfig;
                        result = true;
                        break;
                    }
                }
            }

            if (result) {
                if (mBluetoothLeService != null)
                    mBluetoothLeService.setDeviceUUID(mDeviceConfig.notifyUUIDStr);
                gattHelper.setConfig(mDeviceConfig);
                startService();
                stopScan();
            }

        }
    }


    private boolean handleSingleDevice(DeviceConfig deviceConfig, BluetoothDevice device,byte[] scanRecord) {
        L.d("多名字查询");
        if(deviceConfig.isFilterWithBroadcast() && deviceConfig.doFilterBroadcase(device,scanRecord) ){
            return true;
        }
        for (int i = 0; i < deviceConfig.deviceName.length; i++) {
            if (deviceConfig.deviceName[i].equals(device.getName())
                    || (device.getName().startsWith(deviceConfig.deviceName[i]))) {
                L.d("匹配的device-->" + device.getName());
                L.e("2 多名字找到了设备");
                mDevice = device;
                return true;
            }
        }
        return false;
    }


    private boolean broadcastDeviceConnect;

    private boolean isBroadcastDeviceConnnected() {
        return broadcastDeviceConnect;
    }


    protected void startService() {
        if (mBlueUnit != null) {
            mBlueUnit.startService(mDevice.getAddress());
        }
    }


    public void onDestroy() {
        if (mBluetoothLeService != null) {
            mBluetoothLeService.setBluetoothCallBack(null);
        }
        if (mBlueUnit != null) {
            mBlueUnit.setLeCallBack(null);
            mBlueUnit.stopScan();
            mBlueUnit.stopService();
            mBlueUnit = null;
        }
    }


    public String getVersion() {
        return VERSION;
    }

}
