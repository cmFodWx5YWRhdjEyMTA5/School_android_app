package com.jumper.bluetoothdevicelib.device.bloodpressure;

import com.jumper.bluetoothdevicelib.config.DeviceConfig;
import com.jumper.bluetoothdevicelib.core.ADBlueTooth;

import static com.jumper.bluetoothdevicelib.device.bloodpressure.BloodPressureResult.TESTING;
import static com.jumper.bluetoothdevicelib.device.bloodpressure.BloodPressureResult.TEST_FINISH;

/**
 * Created by Terry on 2016/7/20.
 */
public class BroadcastDeviceBloodPressure extends DeviceConfig {
    public static final String BROADCAST_DEVICE_BLOODPRESSURE = "JPP";

    public BroadcastDeviceBloodPressure() {
        super(new String[]{BROADCAST_DEVICE_BLOODPRESSURE});
    }

    private boolean isNeedShowData;

    @Override
    public BloodPressureResult parseData(byte[] scanRecord, ADBlueTooth adBlueTooth) {
        if (scanRecord == null || scanRecord.length <= 13) return null;
        int d1 = scanRecord[11] & 0xff;//收缩压  106
        int d2 = scanRecord[12] & 0xff;//舒张压   68
        int d3 = scanRecord[13] & 0xff;//心率  62

        if (d1 == 0 && d2 == 0 && d3 == 0) {
            isNeedShowData = true;
            return new BloodPressureResult(TESTING, "-1");
        }
        if (isNeedShowData) {
            isNeedShowData = false;
            return new BloodPressureResult(TEST_FINISH, d2 + "", d1 + "", d3 + "");
        }
        return null;
    }

    @Override
    public boolean isRealData(byte[] datas) {
        return datas != null && datas.length > 13;
    }

    @Override
    public boolean isConnnectedJudgeByData(byte[] datas) {
        return super.isConnnectedJudgeByData(datas);
    }
}
