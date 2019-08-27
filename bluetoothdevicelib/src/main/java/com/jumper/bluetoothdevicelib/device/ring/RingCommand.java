package com.jumper.bluetoothdevicelib.device.ring;

import java.nio.ByteBuffer;

/**
 * Created by Terry on 2018/1/3 17:31.
 *
 * @author Terry
 * @version [版本号, YYYY-MM-DD]
 * @since [产品/模块版本]
 */

public class RingCommand {

    public static final int HEAD_LEN = 5;


    public static final byte HEAD = (byte) 0XA1;


    public static final byte SETTING = (byte) 0X01;


    public static final byte REQUEST = 0X02;


    private InerCommand command;


    private byte[] arr;


    public RingCommand(InerCommand command, boolean isSetting) {
        this.command = command;
        appendData(isSetting);
    }


    private void appendData(boolean isSetting) {
        ByteBuffer buffer = null;
        if (!command.isNeedPackage()) {
            buffer = ByteBuffer.allocate(HEAD_LEN + command.data.length + 1);
            buffer.put((byte) 0x00);//设置 序号
            buffer.put(HEAD);
            buffer.put(isSetting ? SETTING : REQUEST);
            buffer.put(command.type);
            buffer.put((byte) 0x00);
            if (command.data != null) {
                buffer.put((byte) command.data.length);
                buffer.put(command.data, 0, command.data.length);
            }
        }
        if (buffer == null) return;
        this.arr = buffer.array();
    }


    public byte[] getCmd() {
        return this.arr;
    }


}
