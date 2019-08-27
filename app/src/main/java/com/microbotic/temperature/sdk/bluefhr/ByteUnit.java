package com.microbotic.temperature.sdk.bluefhr;

public class ByteUnit {


    public static short[] byteArray2ShortArray(byte[] data, int items) {
        short[] retVal = new short[items];
        for (int i = 0; i < retVal.length; i++)
            retVal[i] = (short) ((data[i * 2] & 0xff) | (data[i * 2 + 1] & 0xff) << 8);

        return retVal;
    }

    public static String bytesToHexString(byte[] paramArrayOfByte) {
        StringBuilder localStringBuilder = new StringBuilder();
        if ((paramArrayOfByte == null) || (paramArrayOfByte.length <= 0))
            return null;
        for (int i = 0; i < paramArrayOfByte.length; i++) {
            String str = Integer.toHexString(0xFF & paramArrayOfByte[i]);
            if (str.length() < 2)
                localStringBuilder.append(0);
            localStringBuilder.append(str).append(" ");
        }
        return localStringBuilder.toString();
    }

}
