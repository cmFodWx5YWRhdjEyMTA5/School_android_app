package com.jumper.bluetoothdevicelib.device.ring;

import com.jumper.bluetoothdevicelib.core.ADBlueTooth;


/**
 * Created by apple on 2017/9/1.
 */

public class RingManager {
    private int index = 1;  //包默认数

    private ADBlueTooth adBlueTooth;

    public RingManager(ADBlueTooth adBlueTooth) {
        this.adBlueTooth = adBlueTooth;
    }

    public void writeBytes(byte[] bytes) {
        adBlueTooth.writeData(bytes);
    }

    public void setPageIndex(int pageIndex) {
        this.index = pageIndex;
    }

    //============================================================================================//
    /**
     * set device time
     */
    public void setDeviceTime() {
        writeBytes(DeviceRingCommand
                .setSystemTime());
    }

    /**
     * set device monitorTime
     */
    public void setDeviceMonitorTime(int no, int mode, int week, int startTimeH, int startTimeM,
                                     int endTimeH, int endTimeM) {
        writeBytes(DeviceRingCommand.setMonitorType(no,
                        mode,week, startTimeH, startTimeM, endTimeH, endTimeM));
    }

    /**
     * set act/def: on/off
     */
    public void setsetRealTimeMonitoring(boolean isActOpen, boolean isDefOpen){
        writeBytes(DeviceRingCommand.setRealTimeMonitoring(isActOpen,isDefOpen));
    }

    /**
     * set delete the record
     */
    public void setDeleteRecord(boolean deleteRecord){
        writeBytes(DeviceRingCommand.deleteRecords(deleteRecord));
    }

    /**
     * set act model
     */
    public void setActModel(boolean actModel){
        writeBytes(DeviceRingCommand.setActModel(actModel));
    }

    /**
     * set ship model
     */
    public void setShipModel(boolean isShip){
        writeBytes(DeviceRingCommand.setShipModel(isShip));
    }
//============================================================================================//

    /**
     * get sys time
     */
    public void getSysTime() {
        getInfoData(DeviceRingCommand.REQ_SYS_TIME);
    }

    /**
     * get device monitor time
     */
    @Deprecated
    public void getMonitorTime() {
        getInfoData(DeviceRingCommand.REQ_TIMING_MONITORING_RECORDS);
    }

    /**
     * get monitorTime state
     */
    public void getMonitorTimeState() {
        getInfoData(DeviceRingCommand.REQ_REAL_TIME_MONITORING_RECORDS);
    }

    /**
     * get monitor record
     */
    public void getMonitorRecord() {
        getInfoData(DeviceRingCommand.REQ_TIMING_MONITORING_RECORDS_DETAIL);
    }

    /**
     * get sys state
     */
    public void getSysState() {
        getInfoData(DeviceRingCommand.REQ_SYS_STATUS);
    }

    //============================================================================================//

    void getInfoData(byte type) {
        writeBytes(new RingCommand(new InerCommand(type, new byte[]{}),false).getCmd());
    }
}
