package com.jumper.bluetoothdevicelib.device.bloodsuger;

import com.jumper.bluetoothdevicelib.config.DeviceConfig;
import com.jumper.bluetoothdevicelib.core.ADBlueTooth;

/**
 * Created by Terry on 2016/7/21.
 */
public class DeviceBloodSuger extends DeviceConfig{

    private  static final String BLOODSUGAR_DEVICE_SERVICE_UUID = "0000fc00-0000-1000-8000-00805f9b34fb";
    private  static final String BLOODSUGAR_CHARACTERISTIC_UUID = "0000fca1-0000-1000-8000-00805f9b34fb";




    private  static final String FMD_BLOOD_SUGER = "Fmd Blood Sugar";

    private  static final String CLINK_BLOOD = "ClinkBlood";


    BloodSugerResult result ;


    public DeviceBloodSuger(){
        super(new String[]{FMD_BLOOD_SUGER,CLINK_BLOOD},
                BLOODSUGAR_DEVICE_SERVICE_UUID,BLOODSUGAR_CHARACTERISTIC_UUID);
    }



    @Override
    public Object parseData(byte[] data, ADBlueTooth adBlueTooth) {

        if (data[5] == data[7]) {
//                    topFragment.setSugarText(Tools.formatNumber(data[7])));
            if(result == null) result = new BloodSugerResult();
            result.clear();
            result.testingCode = data[7];
            return result;
        }
        if (data[4] == 85) {
            return null;
        }

        if(result == null) result = new BloodSugerResult();
        result.clear();

        int d4 = (int) (data[4] & 0xFF);
        int d5 = (int) (data[5] & 0xFF);
        int totalValue = ((d4 << 8) + d5);
        float sugerValue = (float) (totalValue * 1.0 / 18);

        result.value = sugerValue +"";
        return result;
    }
}
