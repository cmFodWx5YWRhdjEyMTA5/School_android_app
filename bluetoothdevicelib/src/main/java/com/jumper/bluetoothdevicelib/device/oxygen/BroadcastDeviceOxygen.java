package com.jumper.bluetoothdevicelib.device.oxygen;

import com.jumper.bluetoothdevicelib.config.DeviceConfig;
import com.jumper.bluetoothdevicelib.core.ADBlueTooth;

/**
 * Created by Administrator on 2017/4/26.
 * <p>
 * 广播式血氧仪解析类
 */

public class BroadcastDeviceOxygen extends DeviceConfig<OxygenResult> {

    private static final String BROADCAST_DEVICE_OXYGEN = "JPO";


    public BroadcastDeviceOxygen() {
        super(new String[]{BROADCAST_DEVICE_OXYGEN});
    }

    @Override
    public OxygenResult parseData(byte[] scanRecord, ADBlueTooth adBlueTooth) {
        if (scanRecord == null || scanRecord.length <= 11) return null;

        int bpm = scanRecord[9] & 0xff;
        int spo2 = scanRecord[10] & 0xff;
        if (-1 == bpm || spo2 > 100) {
            return null;//数据是异常的
        }
        String pi = String.format("%.1f", (float) ((scanRecord[11] & 0xff) * 1.0 / 10));

        return new OxygenResult(spo2 + "", pi, bpm + "");
    }


    @Override
    public boolean isRealData(byte[] datas) {
        return datas != null && datas.length > 11;
    }

    @Override
    public boolean isConnnectedJudgeByData(byte[] datas) {
        return super.isConnnectedJudgeByData(datas);
    }
}
