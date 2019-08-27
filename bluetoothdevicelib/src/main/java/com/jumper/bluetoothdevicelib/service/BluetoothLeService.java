package com.jumper.bluetoothdevicelib.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.jumper.bluetoothdevicelib.core.BlueToothCallback;
import com.jumper.bluetoothdevicelib.helper.L;

import java.util.List;
import java.util.UUID;

@SuppressLint("NewApi")
@SuppressWarnings("unused")
public class BluetoothLeService extends Service {

    private Handler handler = new Handler();

    private final static String TAG = BluetoothLeService.class.getSimpleName();
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private int mConnectionState = STATE_DISCONNECTED;
    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;
    public final static String ACTION_GATT_CONNECTED = "ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED = "ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED = "ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE = "ACTION_DATA_AVAILABLE";
    public final static String ACTION_GATT_WRITE_SUCCESS = "ACTION_GATT_WRITE_SUCCESS";
//	public final static UUID UUID_HEART_RATE_MEASUREMENT = UUID
//			.fromString(SampleGattAttributes.HEART_RATE_MEASUREMENT);



    private BlueToothCallback mBlueToothCallBack;

    private UUID deviceUUID = null;

    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

	private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
		// 鐘舵?杩炴帴鏀瑰彉
		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status,
				int newState) {
			String intentAction;
            if(status != BluetoothGatt.GATT_SUCCESS){
                if(mBlueToothCallBack != null){
                    mBlueToothCallBack.onDisconnect();
                }
                return;
            }
			if (newState == BluetoothProfile.STATE_CONNECTED) {
				intentAction = ACTION_GATT_CONNECTED;
				mConnectionState = STATE_CONNECTED;
				//broadcastUpdate(intentAction);
                if(mBlueToothCallBack != null){
                    mBlueToothCallBack.onConnected();
                }

				handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        boolean bb = mBluetoothGatt.discoverServices();
                    }
                }, 100);
				
				Log.i(TAG, "Connected to GATT server.");
			} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");
//				broadcastUpdate(intentAction);
                if (mBlueToothCallBack != null) {
                    mBlueToothCallBack.onDisconnect();
                }
            }

		}




        // 鍙戠幇鏂版湇鍔＄ 浣庡姛鑰楄澶囧彂鐜?
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
//                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
                if (mBlueToothCallBack != null) {
                    mBlueToothCallBack.onServiceDiscover();
                }
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                L.d("write the message success");
                Intent intent = new Intent(ACTION_GATT_WRITE_SUCCESS);
                sendBroadcast(intent);
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        // 璇诲啓鐗规?
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        // 鏁版嵁鏀瑰彉閫氱煡
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
           // broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            if(mBlueToothCallBack != null){
                mBlueToothCallBack.onDataReceived(characteristic.getValue());
            }
        }


        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
//            super.onDescriptorWrite(gatt, descriptor, status);
            if(mBlueToothCallBack != null){
                mBlueToothCallBack.onNotifySuccess();
            }
        }

    };

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);
        // 杩欐槸蹇冪巼娴嬮噺閰嶇疆鏂囦欢銆?
        if (deviceUUID.equals(characteristic.getUuid())) {
            final byte[] data = characteristic.getValue();
            Bundle bundle = new Bundle();
            bundle.putByteArray("data", data);
            intent.putExtras(bundle);
            sendBroadcast(intent);
        } else {

        }
    }



    public void setBluetoothCallBack(BlueToothCallback callBack){
        this.mBlueToothCallBack = callBack;
    }



    public void setDeviceUUID(String uuid){
        this.deviceUUID = UUID.fromString(uuid);
    }


    public class LocalBinder extends Binder {
        public BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("Terry","---onBind--");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("Terry","---onUnbind--");
        close();
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.d("Terry","---onDestroy--");
        super.onDestroy();
    }

    private final IBinder mBinder = new LocalBinder();

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter
        // through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }

    /*
     * 杩炴帴 true鎴愬姛 flase澶辫触
     */
    public boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG,
                    "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        final BluetoothDevice device = mBluetoothAdapter
                .getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
//		if(SampleGattAttributes.HEART_RATE_MEASUREMENT.equalsIgnoreCase(MyApp_.getInstance().getUuid_heart_rate_measurement().toString())){
//			mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
//		}else{
        mBluetoothGatt = device.connectGatt(this, true, mGattCallback);
//		}


        if (mBluetoothGatt.connect()) { // 杩炴帴杩滅▼璁惧銆?
            mConnectionState = STATE_CONNECTING;//
            Log.d(TAG, "Trying to create a new connection.");
            mBluetoothDeviceAddress = address;
            mConnectionState = STATE_CONNECTING;
            return true;
        } else {
            return false;
        }
    }

    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.disconnect();
    }

    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }


    /**
     * 发送数据
     *
     * @param characteristic
     * @return
     */
    public Boolean write(BluetoothGattCharacteristic characteristic, byte[] bytes) {
//		L.e("mBluetoothGatt=" + mBluetoothGatt.toString());
        if (mBluetoothGatt == null) {
//			L.e("mBluetoothGatt==空");
            return false;
        }
        if (characteristic == null) {
//			L.e("characteristic==空");
            return false;
        }
        characteristic.setValue(bytes);
        return mBluetoothGatt.writeCharacteristic(characteristic);
    }


    public void setCharacteristicNotification(
            BluetoothGattCharacteristic characteristic, boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);

        // This is specific to Heart Rate Measurement.
        if (deviceUUID.equals(characteristic.getUuid())) {
            BluetoothGattDescriptor descriptor = characteristic
                    .getDescriptor(UUID
                            .fromString(CLIENT_CHARACTERISTIC_CONFIG));
            if (descriptor != null) {
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                mBluetoothGatt.writeDescriptor(descriptor);
            }
        }
    }

    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null)
            return null;

        return mBluetoothGatt.getServices();
    }

    public BluetoothGatt getmBluetoothGatt() {
        return mBluetoothGatt;
    }

    public void setmBluetoothGatt(BluetoothGatt mBluetoothGatt) {
        this.mBluetoothGatt = mBluetoothGatt;
    }
}
