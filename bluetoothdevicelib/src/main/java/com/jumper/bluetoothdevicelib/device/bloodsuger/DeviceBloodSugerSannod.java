package com.jumper.bluetoothdevicelib.device.bloodsuger;


import com.jumper.bluetoothdevicelib.config.DeviceConfig;
import com.jumper.bluetoothdevicelib.core.ADBlueTooth;

/**
 * Created by Terry on 2017/8/23 14:18.
 *
 * @author Terry
 * @version [1.2, 2017/8/23]
 * @since [血糖/三诺血糖]
 */

public class DeviceBloodSugerSannod extends DeviceConfig {


    private  static final String SINOCARE_BLOODSUGAR_DEVICE_NAME = "Sinocare";
    private  static final String SINOCARE_BLOODSUGAR_DEVICE_SERVICE_UUID = "0000ffe0-0000-1000-8000-00805f9b34fb";
    private  static final String SINOCARE_BLOODSUGAR_CHARACTERISTIC_UUID = "0000ffe1-0000-1000-8000-00805f9b34fb";

    private BloodSugerResult result;


    public DeviceBloodSugerSannod() {
        super(SINOCARE_BLOODSUGAR_DEVICE_NAME,
                SINOCARE_BLOODSUGAR_DEVICE_SERVICE_UUID, SINOCARE_BLOODSUGAR_CHARACTERISTIC_UUID);

        result = new BloodSugerResult();
    }


    @Override
    public BloodSugerResult parseData(byte[] datas, ADBlueTooth adBlueTooth) {

        if (datas.length != 17) {
            return null;
        }

        int d1 = datas[11] & 0xFF;
        int d2 = datas[12] & 0xFF;
        float sugerValue = (float) (((d1 << 8) + d2) * 1.0 / 10);
        result.clear();
        result.value = sugerValue + "";
        return result;
    }
}
