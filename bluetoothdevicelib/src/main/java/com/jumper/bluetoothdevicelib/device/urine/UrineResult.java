package com.jumper.bluetoothdevicelib.device.urine;

/**
 * Created by cyb on 2015/10/20 0020 下午 11:39 .
 */


//“leu”:”+3”,
//        “nit”:”+”,
//        “ubg”:”+2”,
//        “pro”:”+1”,
//        “ph”:”:5.0,
//        “bld””-”,
//        “sg”:1.005,
//        “ket”:”+”,
//        “bil”:”-”,
//        “glu”:”+-”,
//        “vc”:”+2”

public class UrineResult {

    /**
     * 白细胞
     */
    public String leu;

    /**
     * 亚硝酸盐
     */
    public String nit;
    /**
     * 尿胆原
     */
    public String ubg;
    /**
     * 尿蛋白
     */
    public String pro;
    /**
     * PH值
     */
    public String ph;
    /**
     * 潜血
     */
    public String bld;
    /**
     * 尿比重
     */
    public String sg;
    /**
     * 胴体
     */
    public String ket;
    /**
     * 胆红素
     */
    public String bil;
    /**
     * 尿糖
     */
    public String glu;
    /**
     * 维生素C
     */
    public String vc;

    public String urineCode;//防止重复提交加的字段   数值为设备返回的16进制字符串

    @Override
    public String toString() {
        return "UrineResult{" +
                ", leu='" + leu + '\'' +
                ", nit='" + nit + '\'' +
                ", ubg='" + ubg + '\'' +
                ", pro='" + pro + '\'' +
                ", ph='" + ph + '\'' +
                ", bld='" + bld + '\'' +
                ", sg='" + sg + '\'' +
                ", ket='" + ket + '\'' +
                ", bil='" + bil + '\'' +
                ", glu='" + glu + '\'' +
                ", vc='" + vc + '\'' +
                '}';
    }

    public String getValue(int no) {
        String value = "";
        switch (no) {
            case 0:
                value = leu;
                break;
            case 1:
                value = bld;
                break;
            case 2:
                value = nit;
                break;
            case 3:
                value = ket;
                break;
            case 4:
                value = ubg;
                break;
            case 5:
                value = bil;
                break;
            case 6:
                value = pro;
                break;
            case 7:
                value = glu;
                break;
            case 8:
                value = ph;
                break;
            case 9:
                value = vc;
                break;
            case 10:
                value = sg;
                break;
        }
        return value;
    }

}
