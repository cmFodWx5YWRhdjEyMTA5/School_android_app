package com.jumper.bluetoothdevicelib.device.therapy;

import android.text.TextUtils;

/**
 * Created by yucm on 2017/8/25.
 */

public class TherapyResult {

    private int elec;
    private int mode;
    private int value;
    private String time;
    private String deviceState;

    public TherapyResult(int elec, int mode, int value, String time, String state) {
        this.elec = elec;
        this.mode = mode;
        this.value = value;
        this.time = time;
        this.deviceState = state;
    }

    @Override
    public String toString() {
        return "电量: " + elec
                + "\n" +
                "模式: " + mode
                + "\n" +
                "强度: " + value
                + "\n" +
                "时间: " + time
                + "\n" +
                "设备状态: " + getDeviceState();
    }

    /**
     * 设备状态
     * r-运行，s-停止，l-电极脱落，c-充电中，
     */
    private String getDeviceState() {
        if (TextUtils.isEmpty(deviceState)) return "";
        switch (deviceState) {
            case "r":
                return "正在运行";
            case "s":
                return "停止运行";
            case "l":
                return "电极脱落";
            case "c":
                return "充电中";
        }
        return "";
    }

    public int getElec() {
        return elec;
    }

    public int getMode() {
        return mode;
    }

    public int getValue() {
        return value;
    }

    public String getTime() {
        return time;
    }
}
