package com.jumper.bluetoothdevicelib.device.temperature;

import com.jumper.bluetoothdevicelib.config.DeviceConfig;
import com.jumper.bluetoothdevicelib.core.ADBlueTooth;

/**
 * Created by Terry on 2016/7/19.
 */
public final class BroadcastDeviceTemperature extends DeviceConfig<TemperatureResult> {
    private static final String BROADCAST_DEVICE_TEMPERATURE = "JPT";


    public BroadcastDeviceTemperature() {
        super(new String[]{BROADCAST_DEVICE_TEMPERATURE});
    }

    @Override
    public TemperatureResult parseData(byte[] scanRecord, ADBlueTooth adBlueTooth) {
        if (scanRecord == null || scanRecord.length <= 11) return null;
        int no = scanRecord[9] & 0xff;
        int d1 = scanRecord[10] & 0xff;
        int d2 = scanRecord[11] & 0xff;
        float temperature = (float) (((d1 << 8) + d2) * 1.0 / 100);

        return new TemperatureResult(String.format("%.1f", temperature), no + "");
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
