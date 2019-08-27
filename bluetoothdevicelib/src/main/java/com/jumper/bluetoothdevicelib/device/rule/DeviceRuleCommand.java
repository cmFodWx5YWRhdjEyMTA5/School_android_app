package com.jumper.bluetoothdevicelib.device.rule;

import com.jumper.bluetoothdevicelib.helper.ByteHelper;

import static com.jumper.bluetoothdevicelib.device.rule.DeviceRuleConfig.END_FLAG;
import static com.jumper.bluetoothdevicelib.device.rule.DeviceRuleConfig.START_FLAG;

/**
 * 尺子的组装命令
 * Created by apple on 2017/8/31.
 */

public class DeviceRuleCommand {

    /**
     * 获取电量
     * @return
     */
    public static byte[] getEnergyCommand(){
        return controlDeviceCommand(new byte[]{(byte) 0x01, (byte) 0x00, (byte) 0x00});
    }

    /**
     * 设置部位
     * @param position
     * @return
     */
    public static byte[] setPositionCommand(byte position){
        return controlDeviceCommand(new byte[]{(byte) 0x03, (byte) 0x01, position});
    }

    /**
     * 设置单位的
     * @param unit
     * @return
     */
    public static byte[] setUnitCommand(byte unit){
        return controlDeviceCommand(new byte[]{(byte) 0x05, (byte) 0x01, unit});
    }

    /**
     * 发送命令组装
     * @return
     */
    public static byte[] controlDeviceCommand(byte[] datas) {
        byte[] originalCommand = new byte[14];

        originalCommand[0] = START_FLAG;
        originalCommand[2] = datas[0];
        originalCommand[3] = datas[1];
        originalCommand[4] = datas[2];
        originalCommand[12] = ByteHelper.checkSumOneByte(originalCommand, 2, 11);
        originalCommand[13] = END_FLAG;
        return originalCommand;
    }
}
