package com.jumper.bluetoothdevicelib.device.bloodpressure;

/**
 * Created by Terry on 2016/7/20.
 * 血压结果
 */
public class BloodPressureResult {


    public static final int TESTING = 1;

    public static final int TEST_FINISH = 2;




    public int state;

    //舒张压
    public String dia;

    //收缩压
    public String sys;

    //心率值
    public String heartRate;

    //瞬时 压力值
    public String InstantaneousPressureValue;


    public BloodPressureResult(int state, String instantaneousPressureValue) {
        this.state = state;
        InstantaneousPressureValue = instantaneousPressureValue;
    }


    public BloodPressureResult(int state, String dia, String sys, String heartRate) {
        this.state = state;
        this.dia = dia;
        this.sys = sys;
        this.heartRate = heartRate;
    }


    @Override
    public String toString() {
        return "BloodPressureResult{" +
                "状态|state="+state +"---->"+(state ==TESTING?"测试中":"测试完成")  +
                ", 舒张压|dia='" + dia + '\'' +
                ", 收缩压|sys='" + sys + '\'' +
                ", 心率值|heartRate='" + heartRate + '\'' +
                ", 瞬时 压力值|InstantaneousPressureValue='" + InstantaneousPressureValue + '\'' +
                '}';
    }
}
