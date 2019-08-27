package com.microbotic.temperature.utils;

import android.content.Context;
import android.util.Log;

import com.jumper.bluetoothdevicelib.core.ADBlueTooth;
import com.jumper.bluetoothdevicelib.core.BlueUnit;
import com.jumper.bluetoothdevicelib.device.temperature.BroadcastDeviceTemperature;
import com.jumper.bluetoothdevicelib.device.temperature.DeviceTemperature;
import com.jumper.bluetoothdevicelib.device.temperature.TemperatureResult;
import com.jumper.bluetoothdevicelib.result.Listener;


public class TemperatureDeviceHandler implements Listener<TemperatureResult> {

    private final String tag = TemperatureDeviceHandler.class.getSimpleName();
    private OnTemperatureScanListener temperatureScanListener;

    public void init(Context context, OnTemperatureScanListener temperatureScanListener) {

        this.temperatureScanListener = temperatureScanListener;

        if (!BlueUnit.isHaveBleFeature(context)) {
            String msg = "Device does not support Bluetooth 4.0";
            temperatureScanListener.onError(msg);
        } else if (!BlueUnit.isEnabled(context)) {
            String msg = "Bluetooth is not turned on";
            temperatureScanListener.onError(msg);
        } else {
            ADBlueTooth adBlueTooth = new ADBlueTooth(context, new DeviceTemperature(), new BroadcastDeviceTemperature());
            adBlueTooth.setResultListener(this);
            adBlueTooth.init();
        }
    }

    @Override
    public void onResult(TemperatureResult result) {
        if (result!=null){
            temperatureScanListener.onResult(result);
            return;
        }
        Log.e(tag,"Result is null");
    }

    @Override
    public void onConnectedState(int state) {
        temperatureScanListener.onDeviceConnect(state);
        Log.e(tag,"Device Connected state> "+state);
    }


    public interface OnTemperatureScanListener {
        void onResult(TemperatureResult result);
        void onDeviceConnect(int state);
        void onError(String msg);
    }

}
