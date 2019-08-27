package com.jumper.bluetoothdevicelib.device.rule;

import com.jumper.bluetoothdevicelib.core.ADBlueTooth;

/**
 * Created by apple on 2017/9/1.
 */

public class RuleManager {
    private ADBlueTooth adBlueTooth;
    public RuleManager(ADBlueTooth adBlueTooth) {
        this.adBlueTooth = adBlueTooth;
    }
    public void writeBytes(byte[] bytes){
        adBlueTooth.writeData(bytes);
    }
}
