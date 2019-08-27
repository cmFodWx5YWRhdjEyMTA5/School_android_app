package com.jumper.bluetoothdevicelib.device.weight;

import com.jumper.bluetoothdevicelib.config.DeviceConfig;
import com.jumper.bluetoothdevicelib.core.ADBlueTooth;
import com.jumper.bluetoothdevicelib.helper.WeightTools;

/**
 * Created by Terry on 2016/7/21.
 */
public class DeviceWeightBodyConfig extends DeviceConfig {


    private static final String BROADCAST_DEVICE = "icomon";


    public DeviceWeightBodyConfig() {
        super(new String[]{BROADCAST_DEVICE});
    }

    @Override
    public WeightResult parseData(byte[] data, ADBlueTooth adBlueTooth) {
        WeightResult weightDeviceInfo = WeightHelper.getWeightDeviceInfoByData(data);
        return weightDeviceInfo;
    }


    /**
     * 需要重写进行判断
     *
     * @param datas
     * @return
     */
    @Override
    public boolean isRealData(byte[] datas) {
        return WeightTools.isRealData(datas);
    }

    @Override
    public boolean isConnnectedJudgeByData(byte[] datas) {
        return WeightTools.isConnected(datas);
    }
}
