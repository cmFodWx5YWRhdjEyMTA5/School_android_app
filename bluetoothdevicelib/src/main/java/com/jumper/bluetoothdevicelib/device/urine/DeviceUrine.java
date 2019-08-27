package com.jumper.bluetoothdevicelib.device.urine;

import com.jumper.bluetoothdevicelib.config.DeviceConfig;
import com.jumper.bluetoothdevicelib.core.ADBlueTooth;
import com.jumper.bluetoothdevicelib.helper.ByteHelper;

/**
 * Created by Terry on 2016/7/21.
 */
public class DeviceUrine extends DeviceConfig{


    private static final String EMP_NAME = "BLE-EMP-Ui";
    private static final String EMP_SERVICES_UUID = "000018F0-0000-1000-8000-00805F9B34FB";
    private static final String EMP_CHARACTER_UUID_READ = "00002AF0-0000-1000-8000-00805F9B34FB";
    private static final String EMP_CHARACTER_UUID_WRITE = "00002AF1-0000-1000-8000-00805F9B34FB";



    public static final byte[] cmd =  new byte[]{(byte) 0x93, (byte) 0x8e, 0x04, 0x00, 0x08, 0x04, 0x10};//获取单条信息指令


    private static final String FLAG = "938E";




    private boolean isHaveMoreData = true;

    private String result = "";


    private UrineTools urineTools;
    public DeviceUrine(){
        super(EMP_NAME,EMP_SERVICES_UUID,EMP_CHARACTER_UUID_READ);
        setWriteUUIDstr(EMP_CHARACTER_UUID_WRITE);
        urineTools = new UrineTools();

    }


    @Override
    public UrineResult parseData(byte[] data, ADBlueTooth adBlueTooth) {
        if(isHaveMoreData )result= "";
        if(data != null && data.length  >= 2) {
            if (FLAG.equals(ByteHelper.bytesToHexString(data, 0, 2)) && data.length > 7) {
                result += ByteHelper.bytesToHexString(data, 0, data.length);
                isHaveMoreData = false;
            }

            if (!FLAG.equals(ByteHelper.bytesToHexString(data, 0, 2))) {
                result += ByteHelper.bytesToHexString(data, 0, data.length);
                isHaveMoreData = true;
            }

            if (isHaveMoreData && (!FLAG.equals(ByteHelper.bytesToHexString(data, 0, 2)))) {
                if(result.length() >= 38){
                    urineTools.setResult(result);
                    return urineTools.getUrineInfo();
                }
            }
        }

        return null;
    }
}
