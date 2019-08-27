package com.jumper.bluetoothdevicelib.device.bloodpressure;

import com.jumper.bluetoothdevicelib.config.DeviceConfig;
import com.jumper.bluetoothdevicelib.core.ADBlueTooth;
import com.jumper.bluetoothdevicelib.device.ring.ByteUtil;
import com.jumper.bluetoothdevicelib.helper.ByteHelper;
import com.jumper.bluetoothdevicelib.helper.L;

import static com.jumper.bluetoothdevicelib.device.bloodpressure.BloodPressureResult.TESTING;
import static com.jumper.bluetoothdevicelib.device.bloodpressure.BloodPressureResult.TEST_FINISH;

/**
 * Created by Terry on 2016/7/20.
 */
public class DeviceBloodPressure extends DeviceConfig{

    private static final String BLOODSUPRESSURE_DEVICE_SERVICE_UUID = "0000fff0-0000-1000-8000-00805f9b34fb";
    private static final String BLOODSUPRESSURE_NOTIFYGATT_UUID = "0000fff1-0000-1000-8000-00805f9b34fb";
    private static final String BLOODSUPRESSURE_WRITEGATT_UUID = "0000fff2-0000-1000-8000-00805f9b34fb";



    public static final String name = "Bluetooth BP";






    public DeviceBloodPressure(){
        super(name,BLOODSUPRESSURE_DEVICE_SERVICE_UUID,BLOODSUPRESSURE_NOTIFYGATT_UUID);
        setWriteUUIDstr(BLOODSUPRESSURE_WRITEGATT_UUID);
        this.startCmd = new byte[] { (byte) 0xFD, (byte) 0xFD, (byte) 0xFA, 0x05, 0X0D, 0x0A };
    }




    @Override
    public BloodPressureResult parseData(byte[] data,ADBlueTooth adBlueTooth) {
        L.e(ByteUtil.toHexString(data));
        if(data.length == 1){
            adBlueTooth.writeData(startCmd);
        }else if(data.length == 7){
            return new BloodPressureResult(TESTING, ""+ByteHelper.unsignedByteToInt(data[4]));
        }else if(data.length == 8){
            String shrinkage = ByteHelper.unsignedByteToInt(data[3])+"";
            String diastolic = ByteHelper.unsignedByteToInt(data[4])+"";
            String heartRate = ByteHelper.unsignedByteToInt(data[5])+"";
            return new BloodPressureResult(TEST_FINISH,diastolic,shrinkage,heartRate);
        }
        return null;
    }
}
