package com.jumper.bluetoothdevicelib.device.bloodsuger;

/**
 * Created by Terry on 2016/7/21.
 */
public class BloodSugerResult {


    /**  倒计时 */
    public int testingCode;



    /** 测量的结果值  */
    public String value;



    public void clear(){
        testingCode = 0;
        value = null;
    }


    @Override
    public String toString() {
        return "BloodSugerResult{" +
                "\n(倒计时) testingCode=" + testingCode +
                ", \n(结果 ) value='" + value + '\'' +
                '}';
    }
}
