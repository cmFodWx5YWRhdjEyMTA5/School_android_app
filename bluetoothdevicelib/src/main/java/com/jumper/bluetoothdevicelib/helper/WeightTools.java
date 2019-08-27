package com.jumper.bluetoothdevicelib.helper;


import static com.jumper.bluetoothdevicelib.helper.ByteHelper.bytesToHexString1;

/**
 * Created by Terry on 2016/1/4.
 */
public class WeightTools {


    public static class WeightStateInfo {
        /**
         * 瘦
         */
        public static final int THIN = 0;
        /**
         * 正常
         */
        public static final int NORMAL = 1;
        /**
         * 胖
         */
        public static final int FAT = 2;


        public int state;
        /**
         * 体重偏差
         */
        public String deviation;

        public String normalRange;
    }


    private static final byte[] NO_CONNECTED_FLAG = {(byte) 0xfe, 0x01};


    public static boolean isRealData(byte[] bytes) {
        byte[] data = new byte[8];
        System.arraycopy(bytes, 21, data, 0, 8);
//        L.d("的结果----》" + ByteUnit.bytesToHexString(data));
        byte sum = 0;
        for (int i = 2; i < data.length - 1; i++) {
            sum += data[i];
        }
//        L.d("广播的结果----》" + ByteUnit.bytesToHexString(bytes));
        if (sum == data[7]) {
            if (data[6] == testing || data[6] == result || data[6] == other) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public static boolean isRealData1(byte[] bytes) {
        byte[] data = new byte[17];
        System.arraycopy(bytes, 9, data, 0, 17);
        byte[] data2 = new byte[1];
        System.arraycopy(data, 2, data2, 0, 1);
        if (bytesToHexString1(data2).equals("0a")) {
            L.e("----------------------------------------------------ycm");
            return true;
        }
        return false;
    }

    public static boolean isConnected(byte[] bytes) {
        if (bytes[23] == NO_CONNECTED_FLAG[0] && bytes[24] == NO_CONNECTED_FLAG[1] && bytes[27] == other) {
            return false;
        }
        return true;
    }


    public static final byte testing = (byte) 0xce;
    public static final byte result = (byte) 0xca;
    public static final byte other = (byte) 0xcc;


    public static int unsignedByteToInt(byte b) {
        return (int) b & 0xFF;
    }


}
