package com.jumper.bluetoothdevicelib.device.weight;

import com.jumper.bluetoothdevicelib.helper.ByteHelper;
import com.jumper.bluetoothdevicelib.helper.L;
import com.jumper.bluetoothdevicelib.helper.WeightTools;

/**
 * Created by Terry on 2016/7/21.
 */
public class WeightHelper {



    public static WeightResult getWeightDeviceInfoByData(byte[] bytes){
        L.d("bytes.length====>" + bytes.length);
        WeightResult weightDeviceInfo = new WeightResult();
        if(bytes.length == 16){
            int d4 = (int) (bytes[4] & 0xFF);
            int d5 = (int) (bytes[5] & 0xFF);
            weightDeviceInfo.weightFloat =(float) (((d4 << 8) + d5) * 1.0 / 10);
            weightDeviceInfo.bodyResistance = 0;
            if (bytes[0] == -54) {
                weightDeviceInfo.state = WeightResult.WEIGHT_TESTING;
            } else if (bytes[0] == -50 ) {
                weightDeviceInfo.state = WeightResult.WEIGHT_RESULT;
            }
            weightDeviceInfo.weightType = WeightResult.DEVICE_WEIGHT;
        }else if(bytes.length == 7){
            int d1 = (int) (bytes[1] & 0xFF);
            int d2 = (int) (bytes[2] & 0xFF);
            weightDeviceInfo.weightFloat = (float) (((d1 << 8) + d2) * 1.0 / 10);

            int d3 = (int) (bytes[3] & 0xFF);
            int d4 = (int) (bytes[4] & 0xFF);
            weightDeviceInfo.bodyResistance =  (d3 << 8) + d4;
            if (bytes[0] == -54) {
                weightDeviceInfo.state = WeightResult.WEIGHT_TESTING;
            } else if (bytes[0] == -50 ) {
                weightDeviceInfo.state = WeightResult.WEIGHT_RESULT;
            }
            weightDeviceInfo.weightType = WeightResult.DEVICE_BODY_FAT;
        }else if(bytes.length == 62){
            int a = ByteHelper.unsignedByteToInt(bytes[23]);
            int b = ByteHelper.unsignedByteToInt(bytes[24]);
            float weightFTemp = (float) (((a << 8) + b) * 1.0 / 10);

            if(weightFTemp < 1000)
                weightDeviceInfo.weightFloat  = weightFTemp;
            weightDeviceInfo.bodyResistance = 0;

            if(bytes[27] == WeightTools.testing){
                weightDeviceInfo.state = WeightResult.WEIGHT_TESTING;
            }else if(bytes[27] == WeightTools.result){
                weightDeviceInfo.state = WeightResult.WEIGHT_RESULT;
            }else {
                return null;
            }
            weightDeviceInfo.weightType = WeightResult.DEVICE_WEIGHT;
        }else{
            return null;
        }
        return weightDeviceInfo;
    }



}
