package com.microbotic.temperature.sdk.bluefhr;

/**
 * Created by Administrator on 2016/3/8.
 */
public class BleData {

    public static final int BLE_CONNECTED = 1;
    public static final int BLE_DISCONNECTED = -1;
    public static final int BLE_DATA = 2;

    public int state;

    public byte[] data;


    public BleData(int state, byte[] data) {
        this.state = state;
        this.data = data;
    }


}
