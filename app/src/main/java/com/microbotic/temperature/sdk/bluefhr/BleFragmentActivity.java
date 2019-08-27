package com.microbotic.temperature.sdk.bluefhr;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.microbotic.temperature.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


/**
 * @author Terry<br>
 * @Time 2015年5月15日上午11:53:53<br>
 */
@SuppressLint("NewApi")
public abstract class BleFragmentActivity extends FragmentActivity {


    public static long sScanSpacePeriod = 2000L;

    private Handler mHandler = new Handler(Looper.getMainLooper());


    protected int monitorId;
    protected boolean isFindBoroacastDevice;//在有广播式设备的情况下，是否先找到了广播式设备


    private boolean isBindServise;
    private Handler handler = new Handler(Looper.getMainLooper());

    private Timer timer;
    private TimerTask task;

    protected ArrayList<BluetoothDevice> mLeDevices = new ArrayList<BluetoothDevice>();
    // 连接GATT Serveice
    protected BluetoothAdapter mBluetoothAdapter;
    protected BluetoothDevice mDevice;

    private BleBroadCastRecever myBleRecever;
    private boolean isBleseviceRegiste;

    private boolean isMoreThanOneName = false;

    private int count = 0;
    private boolean isNeedRestart;

    private String macString;

    private boolean isStopedScan = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        myBleRecever = new BleBroadCastRecever();
        registerReceiver(myBleRecever, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));


        if (getDeviceNames() != null) {
            isMoreThanOneName = true;
        }
    }


    public BleFragmentActivity() {
    }


    public void initBlue() {
        isBleseviceRegiste = true;

        // 此时开始搜索蓝牙
        getTipText().setText(getStringRes());


        mHandler.post(reScanRuanble);

    }


    void stopScan() {
        if (mLeScanCallback != null) {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        mHandler.removeCallbacks(reScanRuanble);
        mHandler.removeCallbacks(startRun);
    }


    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, final byte[] scanRecord) {
//			L.e("device name="+device.getName());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //首先若存在广播设备，再若先找到了广播设备，则不再找连接设备；若是先找到了连接设备，
                    // 则进行连接，暂时忽略广播设备直至下次开启新一轮扫描

//					if(true) return;
                    if (hasBroadcastDevice()) {//有两种设备
                        if (getBroadcastDeviceName().equals(device.getName())
                        ) {//找到了广播设备
//							if(!isRealData(scanRecord)){
//								return;
//							}

                            L.d("广播式的结果----");//21
                            if (!isBroadcastDeviceConnnected() && isConnnectedJudgeByData(scanRecord)) {
                                BleData ble = new BleData(BleData.BLE_CONNECTED, scanRecord);
                                postData(ble);
//								if(macString == null)
//									macString = device.getAddress();
//								L.d("找到设备并连上");
                            }
                            L.d("macString----->" + macString);
                            L.d("device.getAddress()--->" + device.getAddress());

                            //if(macString != null && macString.equals(device.getAddress())) {
                            L.d("macString没问题 ");
                            if (!isConnnectedJudgeByData(scanRecord)) {
                                L.d("isBroadcastDeviceConnnected--->" + isBroadcastDeviceConnnected());

                                if (isBroadcastDeviceConnnected())//只有当当前数据是处于未连接状态时的数据且设备当前处于连接状态时发送断开广播
                                {
                                    BleData bleData = new BleData(BleData.BLE_DISCONNECTED, scanRecord);
                                    postData(bleData);
                                    macString = null;
                                    isNeedRestart = false;
                                    isFindBoroacastDevice = false;
                                    count = 0;
                                    L.d("断开连接 ");
                                }
                            } else {
                                BleData bleData = new BleData(BleData.BLE_DATA, scanRecord);
                                postData(bleData);
                                L.d("发送数据 ");
                            }
                            //}

                        }
                    }
                }
            });
        }
    };


    private Runnable startRun = new Runnable() {
        @SuppressLint({"NewApi"})
        public void run() {
            if (mBluetoothAdapter.startLeScan(mLeScanCallback)) {
                L.d("start leScan falied in scanLeDevice");
            } else {
                L.d("mBluetoothAdapter.startLeScan(mLeScanCallback)");
            }

            mHandler.postDelayed(reScanRuanble, sScanSpacePeriod);
        }
    };


    private Runnable reScanRuanble = new Runnable() {
        @SuppressLint({"NewApi"})
        public void run() {
            if (mBluetoothAdapter != null && mLeScanCallback != null) {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }

            mHandler.post(startRun);
        }
    };


    private void broadcastData(final String action, byte[] data) {
        final Intent intent = new Intent(action);
        Bundle bundle = new Bundle();
        bundle.putByteArray("data", data);
        intent.putExtras(bundle);
        sendBroadcast(intent);
    }


    // 后台services,通过ServiceConnection找到IBinder-->service
    private DialogFragment bleDialog;


    protected void showBleDialog() {
        if (!mBluetoothAdapter.isEnabled()) {

            new AlertDialog.Builder(this)
                    .setTitle("温馨提示")
                    .setMessage("与设备连接需要开启蓝牙")
                    .setCancelable(false)
                    .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                            Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                            startActivity(intent);

                            getTipText().setText("请开启蓝牙");
                            getTipText().setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    showBleDialog();
                                }
                            });
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getTipText().setText("请开启蓝牙");
                            getTipText().setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    showBleDialog();
                                }
                            });
                            dialog.dismiss();
                        }
                    })
                    .show();


        } else {
            initBlue();
        }

    }

    protected void askForBle() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showBleDialog();
            }
        }, 500);
    }


    private void postData(BleData bleData) {
        EventBus.getDefault().post(bleData);
    }


    private class BleBroadCastRecever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String stateExtra = BluetoothAdapter.EXTRA_STATE;
            int state = intent.getIntExtra(stateExtra, -1);
            switch (state) {
                case BluetoothAdapter.STATE_TURNING_ON:
                    break;
                case BluetoothAdapter.STATE_ON:
                    getTipText().setText("已开启蓝牙");
                    getTipText().setOnClickListener(null);
                    initBlue();
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    break;
                case BluetoothAdapter.STATE_OFF:
                    break;

            }
        }
    }


    @Override
    protected void onDestroy() {
        stopScan();
        unregisterReceiver(myBleRecever);
        super.onDestroy();

    }

    public abstract BroadcastReceiver getBroadCastReceiver();

    public abstract TextView getTipText();

    public abstract String getDeviceName();

    public int getStringRes() {
        return R.string.ble_scan_;
    }

    public String[] getDeviceNames() {
        return null;
    }

    public abstract String getUUID();


    /**
     * 由子类实现，标示是否有广播设备，目前有 广播式体重秤
     *
     * @return
     */
    public boolean hasBroadcastDevice() {
        return true;
    }

    /**
     * 由子类实现，广播式设备名称，值是否为null依赖于上面的函数
     *
     * @return
     */
    public String getBroadcastDeviceName() {
        return null;
    }


    /*
    由子类实现，根据数据判断广播式设备是否处在发送正常有用数据的状态，若处在则视为连接状态
     */
    protected boolean isConnnectedJudgeByData(byte[] bytes) {
        return true;
    }

    /*
    由子类实现，返回当前广播式设备是否处于连接状态
     */
    protected boolean isBroadcastDeviceConnnected() {
        return false;
    }


}
