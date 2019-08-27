package com.jumper.bluetoothdevicelib.device.oxygen;

import java.util.Arrays;

/**
 * Created by Terry on 2016/7/20.
 */
public class OxygenResult {


    /** 血氧 */
    public String SPO;

    /**  弱冠指数*/
    public String Plus;

    /** 心率 */
    public String heart;

    /** 血氧曲线数据 */
    public byte[] spoDataArray;


    public OxygenResult(){};

    public OxygenResult(String SPO, String plus, String heart) {
        this.SPO = SPO;
        Plus = plus;
        this.heart = heart;
    }

    public OxygenResult(byte[] spoDataArray) {
        this.spoDataArray = spoDataArray;
    }

    public void clear(){
        SPO = null;
        Plus = null;
        heart = null;
        spoDataArray = null;
    }


    @Override
    public String toString() {
        return "OxygenResult{" +
                "SPO='" + SPO + '\'' +
                ", Plus='" + Plus + '\'' +
                ", heart='" + heart + '\'' +
                ", spoDataArray=" + Arrays.toString(spoDataArray) +
                '}';
    }
}
