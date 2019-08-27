package com.jumper.bluetoothdevicelib.device.therapy;

import com.jumper.bluetoothdevicelib.config.DeviceConfig;
import com.jumper.bluetoothdevicelib.core.ADBlueTooth;
import com.jumper.bluetoothdevicelib.helper.L;

/**
 * Created by yucm on 2017/8/25.
 */

public class DeviceTherapy extends DeviceConfig<TherapyResult> {


    private static final String EMP_NAME = "JPD-ES";
    private static final String EMP_SERVICES_UUID = "6e400001-b5a3-f393-e0a9-e50e24dcca9e";
    private static final String EMP_CHARACTER_UUID_READ = "6e400003-b5a3-f393-e0a9-e50e24dcca9e";
    private static final String EMP_CHARACTER_UUID_WRITE = "6e400002-b5a3-f393-e0a9-e50e24dcca9e";


    /*--------------------------理疗指令-----------------------*/
    public static final String MASSAGE_ASCII_ESP = "esp";
    public static final String MASSAGE_ASCII_ESU = "esu";
    public static final String MASSAGE_ASCII_ESD = "esd";
    public static final String MASSAGE_ASCII_ESH = "esh";
    public static final String MASSAGE_ASCII_ESR = "esr";
    public static final String MASSAGE_ASCII_ESV = "esv";


    public static final int THERAPY_MODE_COMBINATION = 0; //模式1 combination
    public static final int THERAPY_MODE_ACUPUNCTURE = 1; //模式2 acupuncture
    public static final int THERAPY_MODE_TAPPING = 2; //模式3 tapping
    public static final int THERAPY_MODE_SCRAPING = 3; //模式4 scraping
    public static final int THERAPY_MODE_MASSAGE = 4; //模式5 massage


    public DeviceTherapy() {
        super(EMP_NAME, EMP_SERVICES_UUID, EMP_CHARACTER_UUID_READ);
        setWriteUUIDstr(EMP_CHARACTER_UUID_WRITE);
    }

    @Override
    public TherapyResult parseData(byte[] datas, ADBlueTooth adBlueTooth) {
        String data = new String(datas);
        L.e(data);

        if (data == null || data.length() < 20) return null;

        int index = data.lastIndexOf("i");
        String elec = data.substring(index - 2, index);
        String item = data.substring(index + 1, index + 2);
        index = data.lastIndexOf("v");
        String value = data.substring(index + 1, index + 3);
        index = data.lastIndexOf("t");
        String time = data.substring(index + 1, index + 6);
        String state = data.substring(index + 6, index + 7);

        return new TherapyResult(paseString(elec), paseString(item), paseString(value), time, state);
    }

    private int paseString(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }


}
