package com.jumper.bluetoothdevicelib.device.rule;


import com.jumper.bluetoothdevicelib.config.DeviceConfig;
import com.jumper.bluetoothdevicelib.core.ADBlueTooth;
import com.jumper.bluetoothdevicelib.helper.ByteHelper;

/**
 * Created by apple on 2017/8/31.
 */

public class DeviceRuleConfig extends DeviceConfig {
    private static final String RULE_NAME = "Gymate_T";
    private static final String RULE_SERVICES_UUID = "0000feb0-0000-1000-8000-00805f9b34fb";
    private static final String RULE_CHARACTER_UUID_READ = "0000feb2-0000-1000-8000-00805f9b34fb";
    private static final String RULE_CHARACTER_UUID_WRITE = "0000feb1-0000-1000-8000-00805f9b34fb";

    public static final byte START_FLAG = (byte) 0xAC;
    public static final byte END_FLAG = (byte) 0xFF;
    public final static byte SEND_DEVICE_ENERGY = (byte) 0x01;   //获取电量
    public final static byte GET_DEVICE_ENERGY = (byte) 0x02;   //获取电量结果

    public final static byte SET_MEASURE_POSITION = (byte) 0x03;   //设置测量部位
    public final static byte SET_MEASURE_UNIT = (byte) 0x05;   //设置测量单位

    public final static byte GET_MEASURE_POSITION_RESULT = (byte) 0x04;   //设置测量部分的结果
    public final static byte GET_MEASURE_UNIT_RESULT = (byte) 0x06;   //设置测量单位的结果

    public final static byte GET_MEASURE_RESULT = (byte) 0x07;   //设置测量数据的结果

    /**
     * 各个部位的标志
     */
    public final static byte POSITION_UNSELECTED = 0x00;//未选择部位
    public final static byte POSITION_SHOULDER = 0x01;  //肩部
    public final static byte POSITION_ARM = 0x02;       //手臂
    public final static byte POSITION_CHEST = 0x03;     //胸部
    public final static byte POSITION_WAIST = 0x04;     //腰部
    public final static byte POSITION_HIP = 0x05;       //臀部
    public final static byte POSITION_HAM = 0x06;       //大腿
    public final static byte POSITION_SHANK = 0x07;     //小腿

    /**
     * 各个单位的标志
     */
    public final static byte UNIT_CM = 0x01;            //厘米
    public final static byte UNIT_INCH = 0x02;          //英尺


    public DeviceRuleConfig() {
        super(RULE_NAME, RULE_SERVICES_UUID, RULE_CHARACTER_UUID_READ);
        setWriteUUIDstr(RULE_CHARACTER_UUID_WRITE);
    }


    @Override
    public RuleResult parseData(byte[] datas, ADBlueTooth adBlueTooth) {
        if (datas.length != 14 ||
                ByteHelper.checkSumOneByte(datas, 2, 11) == datas[12] ||
                datas[0] != START_FLAG ||
                datas[13] != END_FLAG) return null;
        //开始解析
        RuleResult mRule = null;
        switch (datas[2]) {
            case GET_DEVICE_ENERGY: {
                //获取电量
                if (datas[3] == 0x01) {
                    mRule = new RuleResult(datas[4] & 0x0F + (datas[4] & 0xF0));
                }
                break;
            }
            case GET_MEASURE_UNIT_RESULT:
            case GET_MEASURE_POSITION_RESULT: {
                //获取部位设置结果
                mRule = new RuleResult();
                mRule.setWriteSuccess(datas[4] == 0x01 ? 10 : 11);
                break;
            }
            case GET_MEASURE_RESULT: {
                //主动上报的数据
                double data = ((datas[5] & 0xFF) << 8) + (datas[6] & 0xFF);
                mRule = new RuleResult(data / 10, datas[4], datas[7]);
                break;
            }
            default:
                break;
        }
        return mRule;
    }


}
