package com.jumper.bluetoothdevicelib.core;

/**
 * Created by Terry on 2016/7/19.
 */
public interface BlueToothCallback {


    public void onConnected();

    public void onDisconnect();

    public void onDataReceived(byte[] data);

    public void onServiceDiscover();

    public void onNotifySuccess();

    public void onWriteDataReceived(boolean success);


}
