package com.jumper.bluetoothdevicelib.config;

import android.bluetooth.BluetoothDevice;

import com.jumper.bluetoothdevicelib.core.ADBlueTooth;

/**
 * Created by Terry on 2016/7/19.
 */
public abstract class DeviceConfig<T> {


    public String[] deviceName;
    public String notifyUUIDStr;
    public String writeUUIDstr;

    public String serviceUUID;

    public byte[] startCmd;


    public boolean hasBroadcastDevice;


    public DeviceConfig() {

    }


    public DeviceConfig(String deviceName, String serviceUUID, String notifyUUIDStr) {
        this.deviceName = new String[]{deviceName};
        this.serviceUUID = serviceUUID;
        this.notifyUUIDStr = notifyUUIDStr;
    }

    public DeviceConfig(String[] deviceName, String serviceUUID, String notifyUUIDStr) {
        this.serviceUUID = serviceUUID;
        this.deviceName = deviceName;
        this.notifyUUIDStr = notifyUUIDStr;
    }


    public void setWriteUUIDstr(String writeUUIDstr, byte[] startCmd) {
        this.writeUUIDstr = writeUUIDstr;
        this.startCmd = startCmd;
    }


    public void setWriteUUIDstr(String writeUUIDstr) {
        this.writeUUIDstr = writeUUIDstr;
    }

    public DeviceConfig(String[] deviceName) {
        this.deviceName = deviceName;
        this.hasBroadcastDevice = true;
    }


    public abstract T parseData(byte[] datas, ADBlueTooth adBlueTooth);

    /**
     * 根据数据是否正确，不正确则不进行连接或者处理，这个起源于一个广播式的体重称设备
     *
     * @param datas
     * @return
     */
    public boolean isRealData(byte[] datas) {
        return true;
    }


    /**
     * 当读到一个数据datas时是否认为此时已经是连接状态，这个起源于一个广播式的体重称设备，但又想模拟出来一个连接的状态
     *
     * @param datas
     * @return
     */
    public boolean isConnnectedJudgeByData(byte[] datas) {
        return true;
    }


    public byte[] getStartCmd() {
        return null;
    }

    public String[] getNames() {
        return null;
    }



    public boolean isFilterWithBroadcast(){
        return false;
    }

    public boolean doFilterBroadcase(BluetoothDevice device,  byte[] scanRecord){
        // 对广播进行处理
        return false;
    }

}
