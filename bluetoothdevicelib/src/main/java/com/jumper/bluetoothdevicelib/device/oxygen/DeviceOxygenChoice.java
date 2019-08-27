package com.jumper.bluetoothdevicelib.device.oxygen;

import com.jumper.bluetoothdevicelib.config.DeviceConfig;
import com.jumper.bluetoothdevicelib.core.ADBlueTooth;

/**
 * Created by Terry on 2016/7/20.
 */
public class DeviceOxygenChoice extends DeviceConfig{

    private OxygenResult oxygenResult = null;

    private static final String CHAOSI_OXIMETER_DEVICE_NAME = "ChoiceMMed_C208";
    private static final String CHAOSI_OXIMETER_SERVICE_UUID = "00001822-0000-1000-8000-00805f9b34fb";
    private static final String CHAOSI_OXIMETER_CHARACTERISTIC_UUID = "00002a5f-0000-1000-8000-00805f9b34fb";


    public DeviceOxygenChoice(){
        super(CHAOSI_OXIMETER_DEVICE_NAME,CHAOSI_OXIMETER_SERVICE_UUID,CHAOSI_OXIMETER_CHARACTERISTIC_UUID);
    }



    @Override
    public OxygenResult parseData(byte[] data, ADBlueTooth adBlueTooth) {
        if(adBlueTooth.getDeviceConfig().equals(this)){

            if(data.length < 11) return null;

            if(oxygenResult == null)
                oxygenResult =  new OxygenResult();
            oxygenResult.clear();

            int oxygen = (int) (data[1] & 0xFF);
            int pluse = (int) (data[3] & 0xFF);
            double piv = (float) ((data[10] & 0xFF) * 1.0 / 10);


            oxygenResult.heart = pluse +"";
            oxygenResult.SPO = oxygen +"";
            oxygenResult.Plus = piv +"";

            return oxygenResult;

        }
        return null;
    }
}
