package com.jumper.bluetoothdevicelib.device.weight;

/**
 * Created by Terry on 2016/7/21.
 */
public class WeightResult {

    /** 体脂秤*/
    public static final int DEVICE_BODY_FAT = 0;

    /** 体重秤*/
    public static final int DEVICE_WEIGHT = 1;



    /** 测量结果*/
    public static final int WEIGHT_RESULT = 3;

    /** 测量中*/
    public static final int WEIGHT_TESTING = 2;


    /** 人体 体重值 */
    public float weightFloat;

    /**　测量状态 */
    public int state ;

    /** 人体阻抗*/
    public int bodyResistance ;


    /** 体重称类型*/
    public int weightType;


    /** 体脂的一些详细信息*/
    public WeightDetailInfo weightDetailInfo;



    public void clear(){
        weightDetailInfo = null;
        weightFloat = 0;
        bodyResistance = 0;
    }


    @Override
    public String toString() {
        return "WeightResult{" +
                "\n(人体 体重值)weightFloa t=" + weightFloat +
                ",\n (测量状态)state =" + state + "("+(state ==WEIGHT_TESTING?"测量中":"测量结果") +")"+
                ",\n (人体阻抗)bodyResistance=" + bodyResistance +
                ",\n (体重称类型)weightType=" + weightType +"("+(weightType == DEVICE_BODY_FAT?"体脂秤":"体重秤")+")"+
                '}';
    }
}
