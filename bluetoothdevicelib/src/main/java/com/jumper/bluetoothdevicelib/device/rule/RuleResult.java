package com.jumper.bluetoothdevicelib.device.rule;

/**
 * Created by apple on 2017/8/31.
 */

public class RuleResult {
    public double data;    //测量数据
    public byte position;  //测量部位
    public byte unit;      //测量单位
    public int energy;     //电量
    public int writeSuccess;   //写入成功 10 写入失败 11
    public RuleResult(){

    }

    public RuleResult(double data, byte position, byte unit) {
        this.data = data;
        this.position = position;
        this.unit = unit;
    }

    public RuleResult(int energy) {
        this.energy = energy;
    }

    public double getData() {
        return data;
    }

    public void setData(double data) {
        this.data = data;
    }

    public int isWriteSuccess() {
        return writeSuccess;
    }

    public void setWriteSuccess(int writeSuccess) {
        this.writeSuccess = writeSuccess;
    }



    @Override
    public String toString() {
        return "RuleResult{" +
                "data=" + data +
                ", 单位：" + (unit == 1 ?"cm":"inch") +
                ", energy电量值：" + energy  +
                ", writeSuccess=" + writeSuccess ;
    }
}
