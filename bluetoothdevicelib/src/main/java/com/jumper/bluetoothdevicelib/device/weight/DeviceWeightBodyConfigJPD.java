package com.jumper.bluetoothdevicelib.device.weight;

import com.jumper.bluetoothdevicelib.config.DeviceConfig;
import com.jumper.bluetoothdevicelib.core.ADBlueTooth;
import com.jumper.bluetoothdevicelib.helper.ByteHelper;
import com.jumper.bluetoothdevicelib.helper.L;
import com.jumper.bluetoothdevicelib.helper.WeightTools;

/**
 * Created by Terry on 2016/8/15.
 */
public class DeviceWeightBodyConfigJPD extends DeviceConfig{


    private WeightResult weightDeviceInfo = null;

    private WeightDetailInfo weightDetailInfo;


    //---- 沃莱直连
    private  static final String WL_DEVICE  = "icomon";
    private  static final String WL_DEVICE_SERVICE_UUID = "0000ffb0-0000-1000-8000-00805f9b34fb";
    private  static final String WL_DEVICE_CHARACTERISTIC_UUID = "0000ffb2-0000-1000-8000-00805f9b34fb";
    private  static final String WL_DEVICE_WRITE_UUID = "0000ffb1-0000-1000-8000-00805f9b34fb";




    public DeviceWeightBodyConfigJPD(){
        super(new String[]{WL_DEVICE},WL_DEVICE_SERVICE_UUID,WL_DEVICE_CHARACTERISTIC_UUID);
        setWriteUUIDstr(WL_DEVICE_WRITE_UUID);
    }


    @Override
    public Object parseData(byte[] datas, ADBlueTooth adBlueTooth) {
        if(weightDeviceInfo == null){
            weightDeviceInfo = new WeightResult();
        }

        if(datas.length == 8 && datas[7] == ByteHelper.checkSumOneByte(datas, 2, 5)){
            weightDeviceInfo.weightType = WeightResult.DEVICE_BODY_FAT;
            if(WeightTools.testing  == datas[6]){
                weightDeviceInfo.clear();
                weightDeviceInfo.state = WeightResult.WEIGHT_TESTING;
                weightDeviceInfo.weightFloat = (((datas[2]& 0xFF) << 8)+(datas[3]& 0xFF))/10.0f;
            }else if(WeightTools.result == datas[6]){
                weightDeviceInfo.clear();
                weightDeviceInfo.state = WeightResult.WEIGHT_TESTING;
                weightDeviceInfo.weightFloat = (((datas[2]& 0xFF) << 8)+(datas[3]& 0xFF))/10.0f;
                weightDetailInfo = new WeightDetailInfo();
            }else if((byte)0xCB == datas[6]){
                if(weightDetailInfo == null){
                    weightDetailInfo = new WeightDetailInfo();
                }
                weightDeviceInfo.state = WeightResult.WEIGHT_RESULT;

                float data = (((datas[4]& 0xFF) << 8) + (datas[5]& 0xFF)) / 10.0f;
                L.d("data--->" + data);
                L.d("weight--->"+weightDeviceInfo.weightFloat);
                switch (datas[3]) {
                    case 0x00://体重
                        weightDetailInfo.weight = data;
//                            weightDeviceInfo.weightFloat = data;
                        break;
                    case 0x02://体脂率
                        weightDetailInfo.bodyFatRate = getFormatStr(data+"") ;
                        weightDetailInfo.fatMass = getFormatStr((weightDeviceInfo.weightFloat * data / 100)+"");
                        break;
                    case 0x05://"肌肉率"
                        weightDetailInfo.muscle = getFormatStr(data +"");
                        break;
                    case 0x06://基础代谢率
                        weightDetailInfo.basalMetabolism =getFormatStr( (data * 10) +"");
                        break;
                    case 0x07://"骨重量";
                        weightDetailInfo.boneRate = getFormatStr((data *1.0f / weightDeviceInfo.weightFloat *100) +"");
                        break;
                    case 0x08://"水含量"
                        weightDetailInfo.moistureContent = getFormatStr(data+"");
                        break;
                    case (byte)0xFC:
                        weightDeviceInfo.weightDetailInfo = weightDetailInfo;
                        break;
                    default:
                        break;

                }

            }
            return weightDeviceInfo;

        }
        return null;
    }

    private static String getFormatStr(String str) {
        return str.substring(0, !str.contains(".") ? str.length() : str.indexOf(".") + 2);
    }




    private byte[] sendBytesWithByte(byte byte3, byte byte4, byte byte5, byte byte6, byte byte7) {

        byte bytes[] = new byte[8];
        bytes[0] = (byte) 0xAC;
        bytes[1] = (byte) 0x02;
        bytes[2] = byte3;
        bytes[3] = byte4;
        bytes[4] = byte5;
        bytes[5] = byte6;
        bytes[6] = byte7;
        bytes[7] = (byte) ((bytes[2] + bytes[3] + bytes[4] + bytes[5] + bytes[6]) & 0xff);

        L.e("send bytes" + ByteHelper.bytesToHexString(bytes));
        return bytes;

    }

    /** 体重秤初始化信息 1*/
    public byte[] getInitByte (){
        return  sendBytesWithByte((byte) 0xFA, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xCC);
    }

    /** 体重秤初始化信息 2 */
    public byte[] getInitByteWithUserInfo(int age,int height){
        return sendBytesWithByte((byte) 0xFB, (byte) 0x02, (byte) age, (byte) height, (byte) 0xCC);
    }





}
