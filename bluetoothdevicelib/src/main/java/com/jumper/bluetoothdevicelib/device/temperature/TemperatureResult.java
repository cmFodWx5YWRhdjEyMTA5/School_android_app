package com.jumper.bluetoothdevicelib.device.temperature;

import android.text.TextUtils;

import java.text.DecimalFormat;

/**
 * Created by Terry on 2016/7/20.
 */
public class TemperatureResult {

    public  String temperature;
    /**
     * 序列号，开启耳温枪之后点击一次这个序号+1，关机再开机归零
     */
    public String no;

    public int mode;


    public TemperatureResult(String temperature, String no) {
        this.temperature = temperature;
        this.no = no;
    }

    public TemperatureResult(String temperature) {
        this.temperature = temperature;
//        this.no = no;
    }

    public TemperatureResult(String temperature, int mode) {
        this.temperature = temperature;
        this.mode = mode;
    }

    public  float getTemp4C() {
        float C = Float.parseFloat(temperature);
        DecimalFormat decimal = new DecimalFormat("##.#");
        String fm = decimal.format(C);
        return Float.parseFloat(fm);
    }

    /**
     * 温度℃ 转成 F   (℉)=32+摄氏度(℃)×1.8
     */
    public float getTemp4F() {
        if (TextUtils.isEmpty(temperature)) return 0;
        float C = Float.parseFloat(temperature);
        float F = C * 1.8f + 32;
        DecimalFormat decimal = new DecimalFormat("##.#");
        String fm = decimal.format(F);
        return Float.parseFloat(fm);
//        F = (float) (Math.round(F * 10) * 1.0 / 10);
//        return F;
    }

}
