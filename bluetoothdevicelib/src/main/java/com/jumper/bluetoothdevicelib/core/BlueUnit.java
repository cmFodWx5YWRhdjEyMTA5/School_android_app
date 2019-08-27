package com.jumper.bluetoothdevicelib.core;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.jumper.bluetoothdevicelib.helper.L;
import com.jumper.bluetoothdevicelib.service.BluetoothLeService;

import java.util.UUID;

/**
 * Created by Terry on 2016/3/29.
 */
public class BlueUnit {

    public final static long sScanBroadCastSpacePeriod = 2000L;

    public final static long sScanNormalSpacePeriod = 10000L;


    private long mScanSpacePeriod = sScanNormalSpacePeriod;


    private Context mContext;

    private BluetoothAdapter mBluetoothAdapter;

    private LeCallBack mLeCallBack;

    private LeServiceCallBack mLeServiceCallBack;

    private Handler mHandler;

    private boolean isBindServise;

    private ServiceConnection mServiceConnection;


    private String uuid;


//    public void setUuid(String uuid) {
//        this.uuid = uuid;
////    }

    protected BluetoothLeService mBluetoothLeService;

    @SuppressLint({"NewApi"})
    public BlueUnit(Context mContext, Handler handler, boolean hasBroadcastDevice) {
        this.mContext = mContext;
        mHandler = handler;
        BluetoothManager bluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (hasBroadcastDevice)
            mScanSpacePeriod = sScanBroadCastSpacePeriod;

    }


//    public boolean isEnable(){
//        return mBluetoothAdapter.isEnabled();
//    }


    public LeServiceCallBack getLeServiceCallBack() {
        return mLeServiceCallBack;
    }

    public void setLeServiceCallBack(LeServiceCallBack mLeServiceCallBack) {
        this.mLeServiceCallBack = mLeServiceCallBack;
    }

//    public void registerReceiver(BroadcastReceiver receiver){
//        mContext.registerReceiver (receiver, BleServiceHelper.makeGattUpdateIntentFilter());
//    }
//
//
//    public void unRegisterReceiver(BroadcastReceiver receiver){
//        mContext.unregisterReceiver(receiver);
//    }


//    public void setBlueToothCallback(BlueToothCallback mCallback){
//
//    }


    public void startLeScan() {
        mHandler.post(reScanRuanble);
    }

    @SuppressLint({"NewApi"})
    public void stopLeScan() {
        L.e("关闭扫描定时器");
        if (mLeScanCallback != null) {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        mHandler.removeCallbacks(reScanRuanble);
        mHandler.removeCallbacks(startRun);
    }


    protected void startService(String address) {
        Intent gattServiceIntent = new Intent(mContext, BluetoothLeService.class);
        mServiceConnection = new MyServiceConnection(address);
        // 启动services
        isBindServise = mContext.bindService(gattServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    protected void stopService() {
        if (mContext != null && isBindServise) {
            mContext.unbindService(mServiceConnection);
        }
    }


    public boolean write(BluetoothGattCharacteristic characteristic, byte[] bytes) {
        return mBluetoothLeService.write(characteristic, bytes);
    }


    public void setLeCallBack(LeCallBack leCallBack) {
        mLeCallBack = leCallBack;
    }


    @SuppressLint("NewApi")
    public void stopScan() {
        if (mLeScanCallback != null) {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        mHandler.removeCallbacks(reScanRuanble);
        mHandler.removeCallbacks(startRun);
    }


    @SuppressLint({"NewApi"})
    BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            if (mLeCallBack == null) return;
            mLeCallBack.onLeScan(device, rssi, scanRecord);
        }
    };


    private Runnable startRun = new Runnable() {
        @SuppressLint({"NewApi"})
        public void run() {
            L.e("再次开启扫描操作");
            if (TextUtils.isEmpty(uuid)) {
                if (!mBluetoothAdapter.startLeScan(mLeScanCallback)) {
                    L.d("start leScan falied in scanLeDevice");
                } else {
                    L.d("mBluetoothAdapter.startLeScan(mLeScanCallback)");
                }
            } else {//制定serviceuuid的连接
                if (!mBluetoothAdapter.startLeScan(new UUID[]{UUID.fromString(uuid)}, mLeScanCallback)) {
                    L.d("start leScan falied in scanLeDevice");
                } else {
                    L.d("mBluetoothAdapter.startLeScan(mLeScanCallback)");
                }
            }


            mHandler.postDelayed(reScanRuanble, mScanSpacePeriod);
        }
    };


    private Runnable reScanRuanble = new Runnable() {
        @SuppressLint({"NewApi"})
        public void run() {
            if (mBluetoothAdapter != null && mLeScanCallback != null) {
                L.e("停止扫描");
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }
            mHandler.post(startRun);
        }
    };


    private class MyServiceConnection implements ServiceConnection {
        String address;

        public MyServiceConnection(String address) {
            this.address = address;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();

            if (!mBluetoothLeService.initialize()) {
                return;
            }

            Log.d("Terry", "onServiceConnected----->" + service);

//            mBluetoothLeService.setDeviceUUID(uuid);

            if (mLeServiceCallBack != null) {
                mLeServiceCallBack.onMyServiceConnected(mBluetoothLeService);
            }

            mBluetoothLeService.connect(address);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBluetoothLeService = null;
        }
    }


    public interface LeCallBack {
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord);

    }

    public interface LeServiceCallBack {
        public void onMyServiceConnected(BluetoothLeService service);
    }


    public static boolean isHaveBleFeature(Context context) {
        try {

            boolean isHave = context.getPackageManager().hasSystemFeature("android.hardware.bluetooth_le");
            L.d("is support hardware ble-------------------->" + isHave);
            return isHave;
        } catch (Exception e) {
            return false;
        }

    }


    public static boolean isEnabled(Context context) {
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager == null || bluetoothManager.getAdapter() == null) return false;
        return bluetoothManager.getAdapter().isEnabled();
    }

}
