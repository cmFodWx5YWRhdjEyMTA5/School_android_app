package com.jumper.bluetoothdevicelib.device.temperature;

import com.jumper.bluetoothdevicelib.config.DeviceConfig;
import com.jumper.bluetoothdevicelib.core.ADBlueTooth;
import com.jumper.bluetoothdevicelib.helper.ByteHelper;

/**
 * Created by Terry on 2016/7/19.
 */
public final class DeviceTemperature extends DeviceConfig {

    private static final String BLOODSUGAR_DEVICE_SERVICE_UUID = "cdeacb80-5235-4c07-8846-93a37ee6b86d";
    private static final String BLOODSUGAR_CHARACTERISTIC_UUID = "cdeacb81-5235-4c07-8846-93a37ee6b86d";


    private static String[] names = {"Jumper", "My Thermometer"};


    public DeviceTemperature() {
        super(names, BLOODSUGAR_DEVICE_SERVICE_UUID, BLOODSUGAR_CHARACTERISTIC_UUID);
    }


    @Override
    public TemperatureResult parseData(byte[] data, ADBlueTooth adBlueTooth) {


        if (data.length == 5 && -86 == data[0]) {
            int mode = getTempMode(data);
            return new TemperatureResult(getTemperature(data), mode);
        }
        return null;
    }


    private String getTemperature(byte[] data) {
        int a = ByteHelper.unsignedByteToInt(data[2]);
        int b = ByteHelper.unsignedByteToInt(data[3]);
//        DecimalFormat decimal = new DecimalFormat("##.#");
        double tem = ((a << 8) + b) * 1.0 / 100;
//        tem = Math.round(tem * 10) * 1.0 / 10;
//        return decimal.format(tem);
        return tem + "";
    }


    public static int getTempMode(byte[] data) {
        //55为物温模式，22为耳温模式，33为额温模式
        switch (data[1]) {
            case 0x55:
                return TEMP_MODE_55;
            case 0x22:
                return TEMP_MODE_22;
            case 0x33:
                return TEMP_MODE_33;
        }
        return TEMP_MODE_33;
    }

    //1:额温,2:耳温,3:物温
    public static final int TEMP_MODE_55 = 3;
    public static final int TEMP_MODE_22 = 2;
    public static final int TEMP_MODE_33 = 1;
//    public static final int TEMP_MODE_TEMP = 2;
//    public static final int TEMP_MODE_STICK = 1;
}
