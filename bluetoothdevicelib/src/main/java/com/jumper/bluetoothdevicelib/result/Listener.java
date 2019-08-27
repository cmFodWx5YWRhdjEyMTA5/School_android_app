package com.jumper.bluetoothdevicelib.result;

    /**
     * Created by Terry on 2016/7/22.
     */
    public interface Listener<T> {
        public void onResult(T result);

        public void onConnectedState(int state);
    }