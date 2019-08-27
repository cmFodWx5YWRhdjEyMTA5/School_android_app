package com.jumper.bluetoothdevicelib.device.therapy;

import com.jumper.bluetoothdevicelib.core.ADBlueTooth;
import com.jumper.bluetoothdevicelib.helper.L;

import java.util.Timer;
import java.util.TimerTask;

import static com.jumper.bluetoothdevicelib.device.therapy.DeviceTherapy.MASSAGE_ASCII_ESD;
import static com.jumper.bluetoothdevicelib.device.therapy.DeviceTherapy.MASSAGE_ASCII_ESH;
import static com.jumper.bluetoothdevicelib.device.therapy.DeviceTherapy.MASSAGE_ASCII_ESP;
import static com.jumper.bluetoothdevicelib.device.therapy.DeviceTherapy.MASSAGE_ASCII_ESR;
import static com.jumper.bluetoothdevicelib.device.therapy.DeviceTherapy.MASSAGE_ASCII_ESU;
import static com.jumper.bluetoothdevicelib.device.therapy.DeviceTherapy.MASSAGE_ASCII_ESV;

/**
 * Created by yucm on 2017/8/25.
 * <p>
 * 用于发送理疗命令
 */
public class TherapyManager {

    private ADBlueTooth adBlueTooth;
    private Timer mTimer;
    private TimerTask mTimerTask;

    private int mMinute = 15;  //治疗时间  分
    private int mSecond = 0;  //治疗时间  秒
    private int mMode = 0;     //理疗模式
    private int mValue = 1;    //强度

    private boolean isConnected; //连接到设备


    public TherapyManager(ADBlueTooth adBlueTooth) {
        this.adBlueTooth = adBlueTooth;
    }

    private void startTimeTask() {
        if (mTimer != null) {
            stopTimeTask();
        }
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                writeValueAscii(MASSAGE_ASCII_ESH);
            }
        };
        mTimer.schedule(mTimerTask, 1000, 1000);
    }

    private void stopTimeTask() {
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    /**
     * 链接到设备
     */
    public void connectDevice() {
        isConnected = true;
        startTimeTask();
    }

    /**
     * 销毁
     */
    public void onDestroy() {
        stopTimeTask();
    }

    /**
     * 开始工作
     *
     * @param minute 工作多久
     */
    public void startWork(int minute) {
        if (minute == 0) minute = 1;
        if (minute > 15) minute = 15;
        mMinute = minute;
        writeValueAscii(MASSAGE_ASCII_ESR);
    }

    /**
     * 暂停工作
     */
    public void stopWork() {
        writeValueAscii(MASSAGE_ASCII_ESP);
    }

    /**
     * 强度+
     */
    public void addValue() {
        writeValueAscii(MASSAGE_ASCII_ESU);
    }

    /**
     * 强度-
     */
    public void subValue() {
        writeValueAscii(MASSAGE_ASCII_ESD);
    }

    /**
     * 切换模式
     */
    public void swithMode(int mode) {
        if (mode > 4 || mode < 0) return;
        mMode = mode;
        writeValueAscii(MASSAGE_ASCII_ESR);
    }


    /**
     * 发送Ascii指令
     */
    private void writeValueAscii(String command) {
        if (!isConnected) return;
        switch (command) {
            case MASSAGE_ASCII_ESP:
                command = MASSAGE_ASCII_ESP + "00";
                break;
            case MASSAGE_ASCII_ESU:
                command = MASSAGE_ASCII_ESU + "00";
                break;
            case MASSAGE_ASCII_ESD:
                command = MASSAGE_ASCII_ESD + "00";
                break;
            case MASSAGE_ASCII_ESH:
                command = MASSAGE_ASCII_ESH + "00";
                break;
            case MASSAGE_ASCII_ESV:
                if (mValue < 10) {
                    command = MASSAGE_ASCII_ESV + "20" + mValue + "0";
                } else {
                    command = MASSAGE_ASCII_ESV + "2" + mValue + "0";
                }
                break;
            case MASSAGE_ASCII_ESR:
//                this.switchMode = mMode;
                if (mMinute < 10) {
                    if (mSecond < 10) {
                        command = MASSAGE_ASCII_ESR + "5" + mMode + "0" + mMinute + "0" + mSecond + "0";
                    } else {
                        command = MASSAGE_ASCII_ESR + "5" + mMode + "0" + mMinute + "" + mSecond + "0";
                    }
                } else {
                    if (mSecond < 10) {
                        command = MASSAGE_ASCII_ESR + "5" + mMode + "" + (mMinute > 15 ? (mSecond > 0 ? 14 : 15) : mMinute) + "0" + mSecond + "0";
                    } else {
                        command = MASSAGE_ASCII_ESR + "5" + mMode + "" + (mMinute > 15 ? (mSecond > 0 ? 14 : 15) : mMinute) + "" + mSecond + "0";
                    }
                }
                break;
        }
//        logShow("到设备--> " + command);
        L.d("发送指令到设备--------------------> " + command);
        adBlueTooth.writeData(command.getBytes());
    }

}
