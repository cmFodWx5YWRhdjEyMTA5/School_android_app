package com.jumper.bluetoothdevicelib.device.ring;

/**
 * Created by Terry on 2018/1/3 17:54.
 *
 * @author Terry
 * @version [版本号, YYYY-MM-DD]
 * @since [产品/模块版本]
 */

public class InerCommand {


    public static final int REQUEST_TYPE_SETTING = 1;

    public static final int REQUEST_TYPE_REQUEST = 2;




    public byte type;

    public byte[] data;

    public int requestType = REQUEST_TYPE_SETTING;



    public InerCommand(byte type,byte[] data){
        this.type = type;
        this.data = data;
    }

    /**
     * 设置 请求类别。是 （setting）设置 还是 (request)请求
     * @param requestType
     */
    public void setRequestType(int requestType){
        this.requestType = requestType;
    }

    /**
     * 是否需要组包
     * @return
     */
    public boolean isNeedPackage(){
        return data.length + 5 > 20;
    }

    public boolean isSetting(){
        return requestType == REQUEST_TYPE_SETTING;
    }


}
