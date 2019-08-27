package com.jumper.bluetoothdevicelib.device.weight;

import android.text.TextUtils;

import com.jumper.bluetoothdevicelib.config.DeviceConfig;
import com.jumper.bluetoothdevicelib.core.ADBlueTooth;
import com.jumper.bluetoothdevicelib.helper.L;
import com.jumper.bluetoothdevicelib.helper.WeightTools;

import static com.jumper.bluetoothdevicelib.device.weight.WeightResult.DEVICE_WEIGHT;
import static com.jumper.bluetoothdevicelib.device.weight.WeightResult.WEIGHT_RESULT;
import static com.jumper.bluetoothdevicelib.device.weight.WeightResult.WEIGHT_TESTING;
import static com.jumper.bluetoothdevicelib.helper.ByteHelper.bytesToHexString1;

/**
 * Created by Terry on 2016/7/21.
 */
public class DeviceWeightBodyConfigScale extends DeviceConfig {


    private static final String BROADCAST_DEVICE = "Jumper-medical_Scale";

    public DeviceWeightBodyConfigScale() {
        super(new String[]{BROADCAST_DEVICE});
    }


    public static final String WEIGHT_DEVICE_FLAG_01 = "01"; //测量量结果状态(体重和阻抗的值都获取到)
    public static final String WEIGHT_DEVICE_FLAG_02 = "02"; //正常⼴广播状态(⼈人没踩到秤上)
    public static final String WEIGHT_DEVICE_FLAG_03 = "03"; //测量量中状态(脚踩到秤上)
    public static final String WEIGHT_DEVICE_FLAG_04 = "04"; //错误状态(没脱鞋)


    private String mMacAddress; //绑定体重秤的macAddress
    private boolean mMonitoring = false; //监测中...


    @Override
    public WeightResult parseData(byte[] bytes, ADBlueTooth adBlueTooth) {

        byte[] data = new byte[17];
        System.arraycopy(bytes, 9, data, 0, 17);
        byte[] data2 = new byte[1];
        System.arraycopy(data, 2, data2, 0, 1);
        if (!bytesToHexString1(data2).equals("0a")) {
            return null;
        }

        String flag = flag(data);
        String macAddress = macAddress(data);
        float result = result(data);
        L.e("----------------------------------------flag： " + flag);
        //绑定体重秤
        if (result != 0 && TextUtils.equals(WEIGHT_DEVICE_FLAG_03, flag) && TextUtils.isEmpty(mMacAddress)) {
            L.d("----------------------------------------绑定到体重秤： " + macAddress);
            mMacAddress = macAddress;
        }
        //是否是当前绑定秤的数据，不是则过滤
        if (!TextUtils.equals(mMacAddress, macAddress)) {
//            L.e("-----------------------Mac_Address不一样，过滤掉");
            return null;
        }
        //正在测量中...
        if (TextUtils.equals(WEIGHT_DEVICE_FLAG_03, flag)) {
            mMonitoring = true;
        }
        //只有经过监测的流程（有过03状态的数据）才能往下继续，不然就等于来路不明数据，不予处理
        if (!mMonitoring) return null;


        WeightResult weightDeviceInfo = new WeightResult();
        weightDeviceInfo.weightFloat = result;
        weightDeviceInfo.weightType = DEVICE_WEIGHT; //写死 体重秤

        weightDeviceInfo.state = WEIGHT_TESTING; //测量中...
        //解绑设备并保存数据
        if (TextUtils.equals(WEIGHT_DEVICE_FLAG_01, flag) || TextUtils.equals(WEIGHT_DEVICE_FLAG_02, flag) || result == 0) {
            L.d("----------------------------------------解绑体重秤");
            weightDeviceInfo.state = WEIGHT_RESULT; //测量结果
            mMacAddress = null; //解绑体重秤
            mMonitoring = false;
        }
        return weightDeviceInfo;
    }


    private String flag(byte[] data) {
        byte[] data3 = new byte[1];
        System.arraycopy(data, 3, data3, 0, 1);
        return bytesToHexString1(data3);
    }

    private String macAddress(byte[] data) {
        byte[] data6 = new byte[6];
        System.arraycopy(data, 11, data6, 0, 6);
        return bytesToHexString1(data6);
    }

    private float result(byte[] data) {
        byte[] dataW1 = new byte[1];
        System.arraycopy(data, 5, dataW1, 0, 1);
        String data1 = bytesToHexString1(dataW1);
        byte[] dataW2 = new byte[1];
        System.arraycopy(data, 6, dataW2, 0, 1);
        String data2 = bytesToHexString1(dataW2);
        data2 = (data2 + data1);
        return (Integer.parseInt(data2, 16) / 10.0f);
    }


    /**
     * 需要重写进行判断
     *
     * @param datas
     * @return
     */
    @Override
    public boolean isRealData(byte[] datas) {
        return WeightTools.isRealData1(datas);
    }

    @Override
    public boolean isConnnectedJudgeByData(byte[] datas) {
        return super.isConnnectedJudgeByData(datas);
    }
}
