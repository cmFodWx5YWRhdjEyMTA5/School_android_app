package com.jumper.bluetoothdevicelib.device.ring;

import java.util.Calendar;


/**
 * 指环血氧的组装命令
 * Created by apple on 2017/8/31.
 */

public class DeviceRingCommand {


    /**
     * 关闭
     */
    private static final byte CMD_CLOSE = 0x00;
    /**
     * 开启
     */
    private static final byte CMD_OPEN = 0X01;


    private static final byte SET_SYS_TIME = 0X01;//设置系统时间

    private static final byte SET_TIMING_MONITORING = 0x02;//定时监测

    private static final byte SET_REAL_TIME_MONITORING = 0x03;//实时监测

    private static final byte SET_DELETE_MONITORING_RECORDS = 0x04;//删除监测记录

    private static final byte SET_AGING_MODEL = (byte) 0xf0;//老化模式设置

    private static final byte SET_TRANSPORT_MODE = (byte) 0xf1;//运输模式设置


    /**
     * 获取从机  系统时间
     */
    public static final byte REQ_SYS_TIME = 0X01;


    /**
     * 获取定时监测列表
     */
    public static final byte REQ_TIMING_MONITORING_RECORDS = 0X02;

    /**
     * 获取实时监测记录
     */
    public static final byte REQ_REAL_TIME_MONITORING_RECORDS = 0x03;


    /**
     * 获取 定时 监测 的记录
     */
    public static final byte REQ_TIMING_MONITORING_RECORDS_DETAIL = 0x04;


    /**
     * 获取系统状态
     */
    public static final byte REQ_SYS_STATUS = (byte) 0xf0;


    /**
     * set sys time
     */
    public static byte[] setSystemTime() {
        //获取系统时间
        Calendar cal = Calendar.getInstance();
        int mYear = cal.get(Calendar.YEAR);
        byte[] data = {(byte) ((mYear >> 8) & 0xff),
                (byte) (mYear & 0xff),
                (byte) (cal.get(Calendar.MONTH) + 1),
                (byte) (cal.get(Calendar.DAY_OF_MONTH)),
                (byte) (cal.get(Calendar.HOUR_OF_DAY)),
                (byte) (cal.get(Calendar.MINUTE)),
                (byte) (cal.get(Calendar.SECOND))};
        InerCommand inerCommand = new InerCommand(SET_SYS_TIME, data);
        return new RingCommand(inerCommand, true).getCmd();


    }


    public static byte[] setRealTimeMonitoring(boolean isActOpen, boolean isDefOpen) {
        byte[] data = {isActOpen ? CMD_OPEN : CMD_CLOSE, isDefOpen ? CMD_OPEN : CMD_CLOSE};
        InerCommand inerCommand = new InerCommand(SET_REAL_TIME_MONITORING, data);
        return new RingCommand(inerCommand, true).getCmd();
    }


    public static byte[] deleteRecords(boolean delete) {
        byte[] data = {delete ? CMD_OPEN : CMD_CLOSE};
        return new RingCommand(new InerCommand(SET_DELETE_MONITORING_RECORDS, data), true).getCmd();
    }

    public static byte[] setActModel(boolean act) {
        byte[] data = {act ? CMD_OPEN : CMD_CLOSE};
        return new RingCommand(new InerCommand(SET_AGING_MODEL, data), true).getCmd();
    }

    public static byte[] setShipModel(boolean isShip) {
        byte[] data = {isShip ? CMD_OPEN : CMD_CLOSE};
        return new RingCommand(new InerCommand(SET_TRANSPORT_MODE, data), true).getCmd();
    }

    /**
     * 定时监测
     * No ：监测序号 0~ 23
     * MODE ：监测 模式， 0普通 1运动 2睡 眠
     * WEEK ：重复星期，  1~7bit 分别表示星 分别表示星 期一到星日， 0bit0bit 表示设置本次有 效。
     * SHH ：监测开始时间  -- 时
     * SMM ：监测开始时间  —分
     * EHH ：监测结束时间  -- 时
     * EMM ：监测结束时间  —分
     * No/ 00 /00 /00/0000/00/00 00/00 00/00 删除 No 定时， 删除后将从新排序， 删除后将从新排序， 删除后将从新排序，
     */
    public static byte[] setMonitorType(int monitorNo, int monitorMode,
                                        int week,
                                        int startTimeM, int startTimeS,
                                        int endTimeM, int endTimeS) {


        byte[] data = {(byte) monitorNo,
                (byte) monitorMode,
                (byte) week,
                (byte) startTimeM,
                (byte) startTimeS,
                (byte) endTimeM,
                (byte) endTimeS
        };
        return new RingCommand(new InerCommand(SET_TIMING_MONITORING, data), true).getCmd();

    }


    /**
     * 发送请求
     *
     * @param type
     * @return
     */
    public static byte[] request(byte type) {
        InerCommand inerCommand = new InerCommand(type, new byte[]{});
        return new RingCommand(inerCommand, false).getCmd();
    }

    public static void main(String args[]) {
        byte mHex = (byte) 0x1B;
        byte mlow = (byte) 0x6d;
        byte[] data1 = new byte[]{0x01, 0x02, 0x03};
        byte[] data2 = new byte[]{0x04, 0x05, 0x06, 0x09};
        int year = 149;
        System.arraycopy(data1, 1, data2, 0, 2);

        System.out.println(ByteUtil.toHexString((byte) ((year) & 0xff)));
        System.out.println(((mlow & 0xFF) << 8) + (mHex & 0xFF));
        System.out.println(ByteUtil.toHexString((byte) year));
        System.out.println(ByteUtil.toHexString(data2));
        System.out.println(Math.ceil(7021/20));
    }
}



