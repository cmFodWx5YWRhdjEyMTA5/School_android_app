package com.jumper.bluetoothdevicelib.device.oxygen;

import com.jumper.bluetoothdevicelib.config.DeviceConfig;
import com.jumper.bluetoothdevicelib.core.ADBlueTooth;

/**
 * Created by Terry on 2016/7/20.
 */
public class DeviceOxygen extends DeviceConfig{


    private  OxygenResult oxygenResult = null;

    private static final String OXIMETER_DEVICE_NAME1 = "Medical";
    private static final String OXIMETER_DEVICE_NAME2 = "My Oximeter";
    private static final String OXIMETER_SERVICE_UUID = "cdeacb80-5235-4c07-8846-93a37ee6b86d";
    private static final String OXIMETER_CHARACTERISTIC_UUID = "cdeacb81-5235-4c07-8846-93a37ee6b86d";


    public DeviceOxygen(){
        super(new String[]{OXIMETER_DEVICE_NAME1,OXIMETER_DEVICE_NAME2},
                OXIMETER_SERVICE_UUID,OXIMETER_CHARACTERISTIC_UUID);
    }




    @Override
    public OxygenResult parseData(byte[] data, ADBlueTooth adBlueTooth) {

        if(adBlueTooth.getDeviceConfig().equals(this)){

//            L.d("---------获取到数据----------");
            if(data[0] == -127){
                if(oxygenResult == null)
                    oxygenResult =  new OxygenResult();
                oxygenResult.clear();

                int pluse =  data[1] & 0xFF;
                int oxygen =  data[2] & 0xFF;
                int pi =  data[3] & 0xFF;
                double piv = (double) pi * 10 / 100;

                oxygenResult.heart = pluse +"";
                oxygenResult.SPO = oxygen +"";
                oxygenResult.Plus = piv +"";

                return oxygenResult;

            }else if(data[0] == -128 ){

                if(oxygenResult == null)
                    oxygenResult =  new OxygenResult();
                oxygenResult.clear();

                oxygenResult.spoDataArray = data;


                return oxygenResult;
            }

        }
        return null;
    }



}
