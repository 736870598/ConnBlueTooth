package com.sunxiaoyu.connbtcore.dev;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;


import com.sunxiaoyu.connbtcore.listener.ConnListener;
import com.sunxiaoyu.connbtcore.utils.BtDataUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * 连接蓝牙北斗设备
 * Created by sunxiaoyu on 2017/1/20.
 */
public class ConnBTDevice extends ConnDev {

    private final static String TAG = "ConnBTDevice";

    //蓝牙连接相关
    //标示连接状态，是否连接。
    private boolean isConn;

    // 蓝牙连接后的输入输出流
    private InputStream inputStream = null;
    private OutputStream outputStream = null;

    //蓝牙连接相关
    private BluetoothSocket bluetoothSocket;
    private BluetoothDevice device;



    public ConnBTDevice(Context context, BluetoothDevice device, ConnListener connListener) {
        super(context, connListener);
        this.device = device;
    }

    @Override
    public boolean deviceCanUse() {
        return true;
    }

    @Override
    public void connect() {
        if (this.device == null) {
            if (mConnListener != null){
                mConnListener.connException("没有要连接的设备");
                mConnListener = null;
            }
            return;
        }

        //1. 将连接状态设置为false
        isConn = false;


        //开启线程连接蓝牙
        new ConnThread(device).start();
    }


    /**
     * 专门用于连接蓝牙的Thread
     */
    private class ConnThread extends Thread {

        private BluetoothDevice device;

        public ConnThread(BluetoothDevice device) {
            this.device = device;
        }

        @Override
        public void run() {
            super.run();

            //-------开始连接----------
            if (mConnListener != null){
                mConnListener.startConnDevice();
            }

            //3：连接蓝牙
            try {
                if (isSupportReflect()){
                    reflectConnBlueTooth();
                }else{
                    connBlueTooth();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (mConnListener != null){
                    mConnListener.connException("连接时异常,请重新连接");
                    mConnListener = null;
                }
                return;
            }

            //4:获取蓝牙输入输出流
            try {
                inputStream = bluetoothSocket.getInputStream();
                outputStream = bluetoothSocket.getOutputStream();
            } catch (Exception e) {
                e.printStackTrace();
                if (mConnListener != null){
                    mConnListener.connException("获取蓝牙输入输出流时：" + e.getMessage());
                    mConnListener = null;
                }
                return;
            }

            //5: 连接成功，将连接状态设置为true，监听输入输出流
            isConn = true;
            if (mConnListener != null){
                mConnListener.connSuccess();
            }
            receiveBluetoothMag();
        }


        /**
         * 在联想平板上，通过反射方法连接蓝牙时，连接无响应。
         * @return  是否支持反射方法连接
         */
        private boolean isSupportReflect(){
            return false;
//            return ! android.os.Build.MANUFACTURER.equalsIgnoreCase("LENOVO");
        }

        private void connBlueTooth() throws Exception {
            UUID uuId = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
//            bluetoothSocket = device.createRfcommSocketToServiceRecord(uuId);
            bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(uuId);
            bluetoothSocket.connect();
        }

        private void reflectConnBlueTooth() throws Exception {
            Method m = device.getClass().getMethod("createRfcommSocket", int.class);
            bluetoothSocket = (BluetoothSocket) m.invoke(device, 1);
            bluetoothSocket.connect();
        }
    }

    /**
     * 监听输入流。蓝牙--》设备
     * 该方法在非主线程中调用。
     */
    public void receiveBluetoothMag() {
        try {
            while (isConn) {
                byte[] buffer = new byte[512];
                int size = inputStream.read(buffer);
                if (size > 0) {
                    revMsg(buffer, size);
                }
            }
        } catch (Exception e) {
            //连接断开
            if (isConn) {
                isConn = false;
            }
        } finally {
            disconnect();
        }
    }



    @Override
    public boolean disconnect() {
        try {
            //断开连接时释放io流
            if (inputStream != null)
                inputStream.close();
            inputStream = null;

            if (outputStream != null)
                outputStream.close();
            outputStream = null;

            if (bluetoothSocket != null && bluetoothSocket.isConnected())
                bluetoothSocket.close();
            bluetoothSocket = null;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (mConnListener != null){
            mConnListener.connBreak();
            mConnListener = null;
        }

        return true;
    }


    /**
     * 发送蓝牙信息。设备--》蓝牙
     */
    @Override
    public void sendMsg(String msg) {
        try {
            if (outputStream != null && !msg.isEmpty()) {
                outputStream.write(BtDataUtils.hex2Byte(msg));
                outputStream.flush();
            }

            Log.i(TAG, "发送消息到普通蓝牙设备: " + msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
