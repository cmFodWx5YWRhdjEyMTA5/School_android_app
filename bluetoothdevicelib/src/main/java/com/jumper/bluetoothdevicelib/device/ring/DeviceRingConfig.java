package com.jumper.bluetoothdevicelib.device.ring;


import android.bluetooth.BluetoothDevice;

import com.jumper.bluetoothdevicelib.config.DeviceConfig;
import com.jumper.bluetoothdevicelib.core.ADBlueTooth;
import com.jumper.bluetoothdevicelib.helper.L;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * 指环的配置和解析
 */

public class DeviceRingConfig extends DeviceConfig {
    private static final String RING_NAME = "JPD-R500";
    private static final String RING_SERVICES_UUID = "0000fe00-0000-1000-8000-00805f9b34fb";
    private static final String RING_CHARACTER_UUID_WRITE = "0000fe02-0000-1000-8000-00805f9b34fb";
    private static final String RING_CHARACTER_UUID_READ = "0000fe01-0000-1000-8000-00805f9b34fb";

    private static final int MONITOR_FIRST_HEAD_LENGTH = 6;  //监测的header 为6个字节
    private static final int DATA_INTERVAL_LENGTH = 7;       // 数据固定长度


    public static final int REAL_TIME = 0x0011;       //实时数据
    public static final int DEVICE_ANSWER = 0x0012;   //设备应答信息
    public static final int SYSTEM_TIME = 0x0013;     //设备时间
    public static final int DEVICE_MONITOR_TIME = 0x0014;   //设备监测时间

    public static final int DEVICE_RECORD_START = 0x0017;   //实时监测记录
    public static final int DEVICE_RECORDDING = 0x0018;   //实时监测记录获取中
    public static final int DEVICE_RECORD_END = 0x0019;   //实时监测记录结束
    public static final int DEVICE_ACT_BATTERY = 0x0021;   //老化模式F0
    public static final int DEVICE_ACT_DEF = 0x0022;   //获取实时监测状态
    public static final int DEVICE_MONITOR_TIMING = 0x0024;   //
    public static final int DEVICE_MONITOR_END = 0x0026;   //

    public static final int DEVICE_ANSWER_TIME = 0x0025;  //设置时间回答


    public static final int PARSE_DATA_REAL_RECORD = 0x0030;   //实时监测记录
    public static final int PARSE_DATA_REAL_TIMER = 0x0031;   //监测定时列表

    public int tempParseDataIndex = 0x0001;


    SimpleDateFormat sdf = new SimpleDateFormat(" yyyy-MM-dd HH:mm");
    private int tempPageCount = 0;
    private int tempCount = 0;

    private Integer tempIndexCount = null;
    private byte[] tempCopyBytes = new byte[27];
    private int tempByteLenth = 0;
    List<RingRealResult> realResults = new ArrayList<>();
    List<RingMonitorTime> realTimes = new ArrayList<>();
    private boolean replyFlag = false;


    public DeviceRingConfig() {
        super(RING_NAME, RING_SERVICES_UUID, RING_CHARACTER_UUID_READ);
        setWriteUUIDstr(RING_CHARACTER_UUID_WRITE);
    }

    @Override
    public boolean isFilterWithBroadcast() {
        return true;
    }

    @Override
    public boolean doFilterBroadcase(BluetoothDevice device, byte[] scanRecord) {
        if (scanRecord.length >= 31) {
            return scanRecord[9] == 0x4A && scanRecord[10] == 0x44 &&
                    scanRecord[11] == 0x01 && scanRecord[12] == 0x01;
        }
        return false;
    }

    public void setReplyOrNot(boolean flag) {
        this.replyFlag = flag;
    }

    //    80 0001 0162 0241 0388 0400002710 055A


    @Override
    public ResultData parseData(byte[] datas, ADBlueTooth adBlueTooth) {
        if (datas == null) return null;
        ResultData data = new ResultData();
        data.log = ByteUtil.toHexString(datas);
        synchronized (this) {
            L.e("解析类..." + ByteUtil.toHexString(datas));
            switch (datas[0]) {
                case 0x00: {
                    getResultData(datas, data);
                    break;
                }
                default: {
                    //分包的情况
                    parseSubPackage(datas, data);
                    break;
                }
            }
        }

        return data;
    }

    private void getResultData(byte[] datas, ResultData data) {
        switch (datas[2]) {
            // 实时监测
            case 0x01: {
                data.data = parseMonitorData(datas);
                data.type = REAL_TIME;
                break;
            }
            // 应答
            case 0x02: {
                parseAnswer(datas, data);
                break;
            }
        }
    }


    /**
     * 解析设备应答信息
     */
    private void parseAnswer(byte[] datas, ResultData data) {
        if (datas.length < 5) return;//格式不正确
        if (datas.length == 8) {//应答 （设置成功，设置失败等）
            if (replyFlag && datas[3] == 0x03) {
                getDataForActAndDef(datas, data);
            } else if (replyFlag && (datas[3] == (byte) 0xF0)) {
                getDataForActAndBattery(datas, data);
            } else {
                data.type = DEVICE_ANSWER;
                switch (datas[7]) {
                    case 0x00: {
                        data.data = "写入成功";
                        if (datas[3] == 0x02)
                            data.type = DEVICE_ANSWER_TIME;
                        break;
                    }
                    default: {
                        data.data = "写入错误";
                        break;
                    }
                }
            }
        } else {
            parseAnswerFromData(datas, data);
        }

    }

    private void parseAnswerFromData(byte[] datas, ResultData data) {
        switch (datas[3]) {
            case 0x01: {
                int year = ((datas[6] & 0xFF) << 8) + (datas[7] & 0xFF);
                if (year == 0) return;
                int month = datas[8] & 0xFF;
                int dayOfMonth = datas[9] & 0xFF;
                int hour = datas[10] & 0xFF;
                int min = datas[11] & 0xFF;
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month - 1, dayOfMonth, hour, min);

                data.type = SYSTEM_TIME;
                data.data = sdf.format(calendar.getTime());
                break;
            }
            case 0x02: {
                //判断长度
                int length = ((datas[4] & 0xFF) << 8) + (datas[5] & 0xFF);
                if (length == 0) {
                    realTimes.clear();
                    data.type = DEVICE_MONITOR_TIME;
                    data.data = realTimes;
                } else
                    getDataForTimer(datas, data);
                break;
            }
            case 0x03: {

                break;
            }
            case 0x04: {
                getDataForTimerRecord(datas, data);
                break;
            }
            case (byte) 0xF0: {
                getDataForActAndBattery(datas, data);
                break;
            }
        }
    }

    /**
     * 分包的情况处理
     */
    private void parseSubPackage(byte[] datas, ResultData data) {
        switch (tempParseDataIndex) {
            case PARSE_DATA_REAL_RECORD: {
                getDataFromReal(datas, data);
                break;
            }
            case PARSE_DATA_REAL_TIMER: {
                getDataFromTimer(datas, data);
                break;
            }
        }
    }

    /**
     * 解析多包情况下的监测记录
     */
    private synchronized void getDataFromReal(byte[] datas, ResultData data) {
        parseIndex(datas, data, PARSE_DATA_REAL_RECORD);
    }

    /**
     * 定时的分包情况
     */
    private void getDataFromTimer(byte[] datas, ResultData data) {
        //和获取实时监测记录一样的取法
        parseIndex(datas, data, PARSE_DATA_REAL_TIMER);
    }

    private void getDataForTimer(byte[] datas, ResultData data) {
        realTimes.clear();
        parseCommonData(datas, data, PARSE_DATA_REAL_TIMER);
    }


    /**
     * 获取监测记录 null为数据错误
     */
    //依据长度组包
    private void getDataForTimerRecord(byte[] datas, ResultData data) {
        realResults.clear();
        parseCommonData(datas, data, PARSE_DATA_REAL_RECORD);
    }

    private void getDataForActAndBattery(byte[] datas, ResultData data) {
        if (datas.length == 8) {
            RingActAndDef actAndDef = new RingActAndDef();
            actAndDef.battery = ((datas[datas.length - 1] & 0x0F) +
                    (datas[datas.length - 1] & 0xF0)) + "%";
            actAndDef.isAct = datas[datas.length - 2] == 0x01;
            data.data = actAndDef;
            data.type = DEVICE_ACT_BATTERY;
        }
    }

    private void getDataForActAndDef(byte[] datas, ResultData data) {
        if (datas.length == 8) {
            RingActAndDef actAndDef = new RingActAndDef();
            actAndDef.isDef = datas[datas.length - 1] == 0x01;
            actAndDef.isAct = datas[datas.length - 2] == 0x01;
            data.data = actAndDef;
            data.type = DEVICE_ACT_DEF;
        }
    }

    /**
     * 从机监测-实时返回解析
     */
    private RingRealResult parseMonitorData(byte[] datas) {
        if (datas.length <= 20) {
            RingRealResult realResult = new RingRealResult();
            realResult.spo = datas[6] & 0xFF;
            realResult.plus = datas[7] & 0xFF;
            realResult.pi = datas[8] & 0xFF;
            realResult.steps = ByteUtil.toInt(Arrays.copyOfRange(datas, 9, 13));
            return realResult;
        }
        return null;
    }


    private void parseCommonData(byte[] datas, ResultData data, int type) {
        tempParseDataIndex = type;
        tempByteLenth = 0;
        tempCount = 0;
        if (datas.length >= 13) {
            int dataForParseLength = datas.length - MONITOR_FIRST_HEAD_LENGTH;
            tempIndexCount = ((datas[4] & 0xFF) << 8) + (datas[5] & 0xFF);
            tempPageCount = (int) Math.ceil((tempIndexCount -14) / 19.0)  + 1;
            L.e("parseCommonData--" + tempPageCount);
            tempPageCount--;
            if (dataForParseLength % DATA_INTERVAL_LENGTH != 0) {
                tempByteLenth = dataForParseLength
                        % DATA_INTERVAL_LENGTH;
                //拷贝数组到临时存储
                System.arraycopy(datas, datas.length - tempByteLenth,
                        tempCopyBytes, 0, tempByteLenth);
            }
            byte[] byteDatas = Arrays.copyOfRange(datas, MONITOR_FIRST_HEAD_LENGTH, datas.length);
            for (int i = 0; i < dataForParseLength
                    / DATA_INTERVAL_LENGTH; i++) {
                parseSmall(type, byteDatas, i);

            }
            switch (type) {
                case PARSE_DATA_REAL_RECORD: {
                    data.type = tempPageCount == 0 ? DEVICE_RECORD_END :
                            DEVICE_RECORD_START;
                    data.data = realResults;
                    break;
                }
                case PARSE_DATA_REAL_TIMER: {
                    data.type = tempPageCount == 0 ? DEVICE_MONITOR_END :
                            DEVICE_MONITOR_TIME;
                    data.data = realTimes;
                    break;
                }
            }
        }
    }

    private void parseIndex(byte[] datas, ResultData data, int type) {
        if (datas.length + tempByteLenth >= 8) {
            //拷贝数组到临时存储
            tempPageCount--;
            System.arraycopy(datas, 1,
                    tempCopyBytes, tempByteLenth, datas.length - 1);
            L.e("start--"+ByteUtil.toHexString(tempCopyBytes));
            data.type = DEVICE_RECORDDING;
            int indexCount = datas[0] & 0x0F + (datas[0] & 0xF0);
            L.e("包--" + "--" + indexCount + "--" + tempPageCount);

            if ((tempCount == 255 && indexCount == 1) ||
                    (tempCount != 255 && (tempCount + 1 == indexCount))) {
                tempCount = indexCount;
                int dataForParseLength = datas.length - 1 + tempByteLenth;
                for (int i = 0; i < dataForParseLength
                        / DATA_INTERVAL_LENGTH; i++) {
                    parseSmall(type, tempCopyBytes, i);
                }
                switch (type) {
                    case PARSE_DATA_REAL_RECORD: {
                        data.type = DEVICE_RECORDDING;
                        data.data = realResults;
                        break;
                    }
                    case PARSE_DATA_REAL_TIMER: {
                        data.type = DEVICE_MONITOR_TIMING;
                        data.data = realTimes;
                        break;
                    }
                }
                //重新清算临时数组
                if (dataForParseLength % DATA_INTERVAL_LENGTH != 0) {
                    tempByteLenth = dataForParseLength
                            % DATA_INTERVAL_LENGTH;
                    //拷贝数组到临时存储
                    System.arraycopy(datas, datas.length - tempByteLenth,
                            tempCopyBytes, 0, tempByteLenth);
                }else {
                    tempByteLenth = 0;
                }
            } else {
                //错误
                L.e("index--" + "--" + tempCount);
                tempCount = 0;
                realTimes.clear();
                realResults.clear();
                data.data = null;
            }
            if (tempPageCount == 0) {
                data.type = (type == PARSE_DATA_REAL_RECORD ?
                        DEVICE_RECORD_END : DEVICE_MONITOR_END);
            }
        }
    }

    private void parseSmall(int type, byte[] byteDatas, int i) {
        switch (type) {
            case PARSE_DATA_REAL_RECORD: {
                parseAnswerMonitorDatas(Arrays.copyOfRange(byteDatas,
                        i * 7, (i + 1) * 7));
                break;
            }
            case PARSE_DATA_REAL_TIMER: {
                parseAnswerMonitorTimer(Arrays.copyOfRange(byteDatas,
                        i * 7, (i + 1) * 7));
                break;
            }
        }
    }


    /**
     * 定时监测记录
     */
    private void parseAnswerMonitorDatas(byte[] parseDatas) {
        L.e(ByteUtil.toHexString(parseDatas));
        if (parseDatas.length != 7) return;
        RingRealResult realResult = new RingRealResult();
        realResult.month = parseDatas[0] & 0xFF;
        realResult.day = parseDatas[1] & 0xFF;
        realResult.hour = parseDatas[2] & 0xFF;
        realResult.min = parseDatas[3] & 0xFF;
        realResult.spo = parseDatas[4] & 0xFF;
        realResult.plus = parseDatas[5] & 0xFF;
        realResult.pi = parseDatas[6] & 0xFF;
        realResults.add(realResult);
    }

    /**
     * 解析定时监测
     */
    private void parseAnswerMonitorTimer(byte[] parseDatas) {
        L.e(ByteUtil.toHexString(parseDatas));
        RingMonitorTime monitorTime = new RingMonitorTime();
        monitorTime.no = parseDatas[0];
        monitorTime.mode = parseDatas[1];
        monitorTime.week = parseDatas[2];
        monitorTime.startTimeH = parseDatas[3];
        monitorTime.startTimeM = parseDatas[4];
        monitorTime.endTimeH = parseDatas[5];
        monitorTime.endTimeM = parseDatas[6];
        realTimes.add(monitorTime);
    }

}
