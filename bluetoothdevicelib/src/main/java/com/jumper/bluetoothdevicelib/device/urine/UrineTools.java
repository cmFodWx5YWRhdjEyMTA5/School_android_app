package com.jumper.bluetoothdevicelib.device.urine;

import android.util.SparseArray;

import com.jumper.bluetoothdevicelib.helper.L;


/**
 * Created by cyb on 2016/4/7 0007 下午 3:40 .
 * 尿液工具类
 */
public class UrineTools {


    SparseArray<String> LEUMap, NITMap, UBGMap, PROMap, PHMap, BLDMap, SGMap, KETMap, BILMap, GLUMap, VCMap;

    String result;

    public UrineTools() {
        initMap();
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    private void initMap() {
        LEUMap = new SparseArray<String>(8);
        LEUMap.put(0, "-,阴性");
        LEUMap.put(1, "+-,15Cells/μl");
        LEUMap.put(2, "+1,70Cells/μl");
        LEUMap.put(3, "+2,125Cells/μl");
        LEUMap.put(4, "+3,500Cells/μl");
        LEUMap.put(5, "/,/");
        LEUMap.put(6, "/,/");
        LEUMap.put(7, "/,/");

        NITMap = new SparseArray<String>(8);
        NITMap.put(0, "-,阴性");
        NITMap.put(1, "+,阳性");
        NITMap.put(2, "/,/");
        NITMap.put(3, "/,/");
        NITMap.put(4, "/,/");
        NITMap.put(5, "/,/");
        NITMap.put(6, "/,/");
        NITMap.put(7, "/,/");

        UBGMap = new SparseArray<String>(8);
        UBGMap.put(0, "-,0.2/1mg/dl");
        UBGMap.put(1, "+1,2mg/dl");
        UBGMap.put(2, "+2,4mg/dl");
        UBGMap.put(3, "+3,8mg/dl");
        UBGMap.put(4, "/,/");
        UBGMap.put(5, "/,/");
        UBGMap.put(6, "/,/");
        UBGMap.put(7, "/,/");

        PROMap = new SparseArray<String>(8);
        PROMap.put(0, "-,阴性");
        PROMap.put(1, "+-,微量");
        PROMap.put(2, "+1,30mg/dl");
        PROMap.put(3, "+2,100mg/dl");
        PROMap.put(4, "+3,300mg/dl");
        PROMap.put(5, "+4,>2000mg/dl");
        PROMap.put(6, "/,/");
        PROMap.put(7, "/,/");

        PHMap = new SparseArray<String>(8);
        PHMap.put(0, "5.0,5.0");
        PHMap.put(1, "6.0,6.0");
        PHMap.put(2, "6.5,6.5");
        PHMap.put(3, "7.0,7.0");
        PHMap.put(4, "7.5,7.5");
        PHMap.put(5, "8.0,8.0");
        PHMap.put(6, "8.5,8.5");
        PHMap.put(7, "/,/");

        BLDMap = new SparseArray<String>(8);
        BLDMap.put(0, "-,阴性");
        BLDMap.put(1, "+-,微量");
        BLDMap.put(2, "+1,25Cells/μl");
        BLDMap.put(3, "+2,80Cells/μl");
        BLDMap.put(4, "+3,200Cells/μl");
        BLDMap.put(5, "/,/");
        BLDMap.put(6, "/,/");
        BLDMap.put(7, "/,/");

        SGMap = new SparseArray<String>(8);
        SGMap.put(0, "1.000,1.000");
        SGMap.put(1, "1.005,1.005");
        SGMap.put(2, "1.010,1.010");
        SGMap.put(3, "1.015,1.015");
        SGMap.put(4, "1.020,1.020");
        SGMap.put(5, "1.025,1.025");
        SGMap.put(6, "1.030,1.030");
        SGMap.put(7, "/,/");

        KETMap = new SparseArray<String>(8);
        KETMap.put(0, "-,阴性");
        KETMap.put(1, "+-,5mg/dl");
        KETMap.put(2, "+1,15mg/dl");
        KETMap.put(3, "+2,40mg/dl");
        KETMap.put(4, "+3,80mg/dl");
        KETMap.put(5, "+4,160mg/dl");
        KETMap.put(6, "/,/");
        KETMap.put(7, "/,/");

        BILMap = new SparseArray<String>(8);
        BILMap.put(0, "-,阴性");
        BILMap.put(1, "+1,少量");
        BILMap.put(2, "+2,中量");
        BILMap.put(3, "+3,大量");
        BILMap.put(4, "/,/");
        BILMap.put(5, "/,/");
        BILMap.put(6, "/,/");
        BILMap.put(7, "/,/");

        GLUMap = new SparseArray<String>(8);
        GLUMap.put(0, "-,阴性");
        GLUMap.put(1, "+-,100mg/dl");
        GLUMap.put(2, "+1,250mg/dl");
        GLUMap.put(3, "+2,500mg/dl");
        GLUMap.put(4, "+3,1000mg/dl");
        GLUMap.put(5, "+4,>2000mg/dl");
        GLUMap.put(6, "/,/");
        GLUMap.put(7, "/,/");

        VCMap = new SparseArray<String>(8);
        VCMap.put(0, "-,阴性");
        VCMap.put(1, "+-,0.6mmol/l");
        VCMap.put(2, "+1,1.4mmol/l");
        VCMap.put(3, "+2,2.8mmol/l");
        VCMap.put(4, "+3,5.6mmol/l");
        VCMap.put(5, "/,/");
        VCMap.put(6, "/,/");
        VCMap.put(7, "/,/");

    }


    public enum Urine {
        LEU, NIT, UBG, PRO, PH, BLD, SG, KET, BIL, GLU, VC;
    }

    public String getTraditioalNameByUrineTypeAndMachinename(Urine urine, String Machinename) {
        SparseArray<String> temp = getMapByUrineType(urine);
        for (int i = 0; i < temp.size(); i++) {
            String[] str = temp.get(i).split(",");
            if (str[0].equals(Machinename)) {
                return str[1];
            }
    }
        return "";
    }


    //有类型得到设备返回的int值大小
    public String getVaule(Urine item) {
        return getMapByUrineType(item).get(getIntValue(item), "");
    }

    //得到机器码
    public String getMachineVaule(Urine item) {
        String[] strs = getVaule(item).split(",");
        if (strs.length >= 1) {
            return strs[0];
        }
        return "";
    }

    //有效数据是从第四行开始 的
    public String getUsefulString(String string) {
        return string.substring(24, 36);
    }

    //根据顺序得到各项监测数据的int值
    public int getIntValue(Urine item) {
        String binStr = hexString2binaryString(getUsefulString(result));
        String resultStr = "";
        switch (item) {
            case LEU:
                resultStr = binStr.substring(2, 5);
                break;
            case BLD:
                resultStr = binStr.substring(17, 20);
                break;
            case NIT:
                resultStr = binStr.substring(29, 32);
                break;
            case KET:
                resultStr = binStr.substring(42, 45);
                break;
            case UBG:
                resultStr = binStr.substring(26, 29);
                break;
            case BIL:
                resultStr = binStr.substring(39, 42);
                break;
            case PRO:
                resultStr = binStr.substring(23, 26);
                break;
            case GLU:
                resultStr = binStr.substring(36, 39);
                break;
            case PH:
                resultStr = binStr.substring(20, 23);
                break;
            case VC:
                resultStr = binStr.substring(33, 36);
                break;
            case SG:
                resultStr = binStr.substring(45, 48);
                break;
        }
        try {
            return Integer.parseInt(resultStr, 2);
        } catch (NumberFormatException e) {
            return 0;
        }
    }



    //16进制转二进制
    public  String hexString2binaryString(String hexString) {
        if (hexString == null || hexString.length() % 2 != 0)
            return null;
        String bString = "", tmp;
        for (int i = 0; i < hexString.length(); i++) {
            tmp = "0000" + Integer.toBinaryString(Integer.parseInt(hexString.substring(i, i + 1), 16));
            bString += tmp.substring(tmp.length() - 4);
        }
        return bString;
    }

    //有类型得到map集合
    public SparseArray<String> getMapByUrineType(Urine item) {
        SparseArray<String> tempMap = new SparseArray<String>();
        switch (item) {
            case LEU:
                tempMap = LEUMap;
                break;
            case BLD:
                tempMap = BLDMap;
                break;
            case NIT:
                tempMap = NITMap;
                break;
            case KET:
                tempMap = KETMap;
                break;
            case UBG:
                tempMap = UBGMap;
                break;
            case BIL:
                tempMap = BILMap;
                break;
            case PRO:
                tempMap = PROMap;
                break;
            case GLU:
                tempMap = GLUMap;
                break;
            case PH:
                tempMap = PHMap;
                break;
            case VC:
                tempMap = VCMap;
                break;
            case SG:
                tempMap = SGMap;
                break;
        }

//        return tempMap.get(getIntValue(item), "");
        return tempMap;
    }


    //得到传统结果形式
    public String getTraditionalVaule(Urine item) {
        String[] strs = getVaule(item).split(",");
        if (strs.length >= 2) {
            return strs[1];
        }
        return "";
    }


    public UrineResult getUrineInfo() {
        UrineResult info = new UrineResult();
        info.leu = getMachineVaule(Urine.LEU);
        info.nit = getMachineVaule(Urine.NIT);
        info.ubg = getMachineVaule(Urine.UBG);
        info.pro = getMachineVaule(Urine.PRO);
        info.ph = getMachineVaule(Urine.PH);
        info.sg = getMachineVaule(Urine.SG);
        info.ket = getMachineVaule(Urine.KET);
        info.bil = getMachineVaule(Urine.BIL);
        info.glu = getMachineVaule(Urine.GLU);
        info.vc = getMachineVaule(Urine.VC);
        info.bld = getMachineVaule(Urine.BLD);
        return info;
    }

    public static boolean checkInfoRight(UrineResult info) {
        int time = 0;
        for (int i = 0; i < 10; i++) {
            if (info.getValue(i).equals("/")) {
                time++;
            }
        }
        return !(time == 10);
    }

    //将后台返回的机器语言翻译为传统语言
    public UrineResult getTraditioalNameByUrineTypeAndMachinename(UrineResult info) {
        UrineResult info1 = new UrineResult();
        L.e(info.toString());
        info1.leu = getTraditioalNameByUrineTypeAndMachinename(Urine.LEU, info.leu);
        info1.nit = getTraditioalNameByUrineTypeAndMachinename(Urine.NIT, info.nit);
        info1.ubg = getTraditioalNameByUrineTypeAndMachinename(Urine.UBG, info.ubg);
        info1.pro = getTraditioalNameByUrineTypeAndMachinename(Urine.PRO, info.pro);
//        info1.ph = getTraditioalNameByUrineTypeAndMachinename(Urine.PH, info.ph);
//        info1.sg = getTraditioalNameByUrineTypeAndMachinename(Urine.SG, info.sg);
        info1.ph = info.ph;
        info1.sg = info.sg;
        info1.ket = getTraditioalNameByUrineTypeAndMachinename(Urine.KET, info.ket);
        info1.bil = getTraditioalNameByUrineTypeAndMachinename(Urine.BIL, info.bil);
        info1.glu = getTraditioalNameByUrineTypeAndMachinename(Urine.GLU, info.glu);
        info1.vc = getTraditioalNameByUrineTypeAndMachinename(Urine.VC, info.vc);
        info1.bld = getTraditioalNameByUrineTypeAndMachinename(Urine.BLD, info.bld);
        return info1;
    }
}
