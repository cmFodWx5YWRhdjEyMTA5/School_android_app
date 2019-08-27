package com.jumper.bluetoothdevicelib.core;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;


import com.jumper.bluetoothdevicelib.config.DeviceConfig;
import com.jumper.bluetoothdevicelib.helper.L;

import java.util.List;

/**
 * Created by Terry on 2016/3/29.
 */
public class GattHelper {


    private String serviceUUID;

    private String notifyGattCharacteristicUUID;

    private String writeGattCharacteristicUUID;


    private BluetoothGattCharacteristic writeGattCharacteristic;

    private BluetoothGattCharacteristic notifyGattCharacteristic;


    private onDisConverNotifyAndWrite onDisConverNotifyAndWrite;

    private BlueUnit blueUnit;


    public GattHelper(BlueUnit blueUnit) {
        this.blueUnit = blueUnit;
    }

    public void setOnDisConverNotifyAndWrite(onDisConverNotifyAndWrite onDisConverNotifyAndWrite) {
        this.onDisConverNotifyAndWrite = onDisConverNotifyAndWrite;
    }


    public void setConfig(String serviceUUID, String notifyGattCharacteristicUUID,
                          String writeGattCharacteristicUUID) {
        this.serviceUUID = serviceUUID;
        this.notifyGattCharacteristicUUID = notifyGattCharacteristicUUID;
        this.writeGattCharacteristicUUID = writeGattCharacteristicUUID;
    }


    public void setConfig(DeviceConfig deviceConfig){
        setConfig(deviceConfig.serviceUUID,deviceConfig.notifyUUIDStr,
                deviceConfig.writeUUIDstr);
    }




    public void setConfig(String serviceUUID, String notifyGattCharacteristicUUID) {
        setConfig(serviceUUID, notifyGattCharacteristicUUID, null);
    }


    //已经根据设备名字连接上了设备，现在根据serviceuuid和characteristicuuid打开相应数据通道
    @SuppressLint("NewApi")
    public void doServiceDiscovered(List<BluetoothGattService> gattServices, boolean setNotifyAble) {
//        for (BluetoothGattService gattService : gattServices){
//            String uuid0 = gattService.getUuid().toString();
//            L.e("ServiceUUID---:" + uuid0);
//            List<BluetoothGattCharacteristic> gattCharacteristics1 = gattService.getCharacteristics();
//            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics1) {
//                String uuid11 = gattCharacteristic.getUuid().toString();
//                L.e("CharacteristicUUID---:" + uuid11);
//            }
//        }
//        L.e("--------------------------------------");
        for (BluetoothGattService gattService : gattServices) {
            String uuid = gattService.getUuid().toString();
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
            L.e("ServiceUUID:" + uuid);
            if (uuid.equalsIgnoreCase(serviceUUID)) {
                for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                    String uuid1 = gattCharacteristic.getUuid().toString();
                    L.e("CharacteristicUUID:" + uuid1);
                    if (uuid1.equalsIgnoreCase(notifyGattCharacteristicUUID)) {
                        if (setNotifyAble) {
                            this.blueUnit.mBluetoothLeService
                                    .setCharacteristicNotification(gattCharacteristic, true);
                        }
                        notifyGattCharacteristic = gattCharacteristic;
                        if (onDisConverNotifyAndWrite != null) {
                            onDisConverNotifyAndWrite.onDisConverNotify(uuid1);
                        }
                    }
                    if (uuid1.equalsIgnoreCase(writeGattCharacteristicUUID)) {
                        writeGattCharacteristic = gattCharacteristic;
                        if (onDisConverNotifyAndWrite != null) {
                            onDisConverNotifyAndWrite.onDisConverWrite(uuid1);
                        }
                    }
                }
            }
        }
    }

    //已经根据设备名字连接上了设备，现在根据serviceuuid和characteristicuuid打开相应数据通道,对上面的方法进行了重载,因为当有两个设备时需要根据设备不同进行切换各uuid
    @SuppressLint("NewApi")
    public void doServiceDiscovered(List<BluetoothGattService> gattServices, String serviceUUID, String notifyGattCharacteristicUUID, boolean setNotifyAble) {
//        for (BluetoothGattService gattService : gattServices){
//            String uuid0 = gattService.getUuid().toString();
//            L.e("ServiceUUID---:" + uuid0);
//            List<BluetoothGattCharacteristic> gattCharacteristics1 = gattService.getCharacteristics();
//            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics1) {
//                String uuid11 = gattCharacteristic.getUuid().toString();
//                L.e("CharacteristicUUID---:" + uuid11);
//            }
//        }
//        L.e("--------------------------------------");

        setConfig(serviceUUID, notifyGattCharacteristicUUID);
        for (BluetoothGattService gattService : gattServices) {
            String uuid = gattService.getUuid().toString();
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
            L.e("ServiceUUID:" + uuid);
            if (uuid.equalsIgnoreCase(serviceUUID)) {
                for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                    String uuid1 = gattCharacteristic.getUuid().toString();
                    L.e("CharacteristicUUID:" + uuid1);
                    if (uuid1.equalsIgnoreCase(notifyGattCharacteristicUUID)) {
                        if (setNotifyAble) {
                            this.blueUnit.mBluetoothLeService
                                    .setCharacteristicNotification(gattCharacteristic, true);
                        }
                        notifyGattCharacteristic = gattCharacteristic;
                        if (onDisConverNotifyAndWrite != null) {
                            onDisConverNotifyAndWrite.onDisConverNotify(uuid1);
                        }
                    }
                    if (uuid1.equalsIgnoreCase(writeGattCharacteristicUUID)) {
                        writeGattCharacteristic = gattCharacteristic;
                        if (onDisConverNotifyAndWrite != null) {
                            onDisConverNotifyAndWrite.onDisConverWrite(uuid1);
                        }
                    }
                }
            }
        }
    }


    public BluetoothGattCharacteristic getNotifyGattCharacteristic() {
        return notifyGattCharacteristic;
    }

    public void setNotifyGattCharacteristic(BluetoothGattCharacteristic notifyGattCharacteristic) {
        this.notifyGattCharacteristic = notifyGattCharacteristic;
    }

    public BluetoothGattCharacteristic getWriteGattCharacteristic() {
        return writeGattCharacteristic;
    }

    public void setWriteGattCharacteristic(BluetoothGattCharacteristic writeGattCharacteristic) {
        this.writeGattCharacteristic = writeGattCharacteristic;
    }


    public void writeValue(byte[] bytes) {
        this.blueUnit.write(writeGattCharacteristic, bytes);
    }


    public void setCharacteristicNotification() {
        this.blueUnit.mBluetoothLeService.setCharacteristicNotification(
                notifyGattCharacteristic, true
        );
    }

    public interface onDisConverNotifyAndWrite {
        void onDisConverNotify(String UUID);

        void onDisConverWrite(String UUID);
    }


}
