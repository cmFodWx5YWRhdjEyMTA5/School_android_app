package com.jumper.bluetoothdevicelib.device.ring;

/**
 * Created by apple on 2017/11/13.
 */

public class RingRealResult {
    public int dexCount ;
    public int spo;
    public int plus;
    public Integer pi;
    public Integer steps;
    public Integer energy;
    public String startTime;
    public int spitTime;
    public int month;
    public int day;
    public  int hour;
    public  int min;


    @Override
    public String toString() {
        return "数据分析" +
                "---包数 :" + dexCount +
                " , Spo2 :" + spo +
                " , PR :" + plus +
                " , Pi :" + pi.doubleValue()/10 +
                " , 步数 :" + steps +
                " , 电量 :" + energy +
                "%";
    }
}
