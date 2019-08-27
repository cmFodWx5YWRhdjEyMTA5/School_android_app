package com.jumper.bluetoothdevicelib.device.ring;

/**
 * 监测时间
 * Created by apple on 2017/11/13.
 */

public class RingMonitorTime {
    public int no ;
    public int mode;
    public int week;
    public int startTimeH;
    public int startTimeM;
    public int endTimeH;
    public int endTimeM;

    @Override
    public String toString() {
        return "监测时间--" +
                "监测序号 ：" + no +
                ", 监测模式 ：" + mode +
                ", 重复周期 ：" + week +
                ", 开始时间 ：" + startTimeH +
                "：" + startTimeM +
                ", 结束时间 ：" + endTimeH +
                "：" + endTimeM +
                '}';
    }
}
