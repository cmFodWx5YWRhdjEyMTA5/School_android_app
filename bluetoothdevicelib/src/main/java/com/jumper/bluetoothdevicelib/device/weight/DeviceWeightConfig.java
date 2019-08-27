package com.jumper.bluetoothdevicelib.device.weight;

import com.jumper.bluetoothdevicelib.config.DeviceConfig;
import com.jumper.bluetoothdevicelib.core.ADBlueTooth;

/**
 * Created by Terry on 2016/7/21.
 */
public class DeviceWeightConfig extends DeviceConfig{



    private  static final String WHITE_DEVICE = "ElecScalesBH";
    private  static final String RED_DEVICE = "SWAN";




    private  static final String WHITE_DEVICE_SERVICE_UUID = "0000fff0-0000-1000-8000-00805f9b34fb";
    private  static final String WHITE_DEVICE_CHARACTERISTIC_UUID = "0000fff4-0000-1000-8000-00805f9b34fb";



    public DeviceWeightConfig(){
        super(new String[]{WHITE_DEVICE,RED_DEVICE},WHITE_DEVICE_SERVICE_UUID,
                WHITE_DEVICE_CHARACTERISTIC_UUID);

    }




    @Override
    public WeightResult parseData(byte[] data, ADBlueTooth adBlueTooth) {
        WeightResult weightDeviceInfo = WeightHelper.getWeightDeviceInfoByData(data);
        return weightDeviceInfo;
    }








}
