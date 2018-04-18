package com.sunxiaoyu.connbtcore.dev;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;


import com.sunxiaoyu.connbtcore.listener.ConnListener;

import java.util.List;

/**
 * 连接北斗盒子等ble蓝牙设备。
 * <p>
 * Created by sunxiaoyu on 2017/1/19.
 */
public class ConnBLEDevice extends ConnDev {

    private final static String TAG = "ConnBLEDevice";

    //HMSoft 4.0蓝牙设备的读写模块的uuid
    private String readUUid;
    private String writeUUid;


    //连接的蓝牙设备
    private BluetoothDevice device;

    //连接的Gatt
    private BluetoothGatt mBluetoothGatt;
    private BluetoothGattCallback mBluetoothGattCallBack;

    //标示是否连接中
    private boolean isConning;

    //读写模块的Characteristic
    private BluetoothGattCharacteristic writeCharacteristic;
    private BluetoothGattCharacteristic readCharacteristic;

    public ConnBLEDevice(Context context, BluetoothDevice device, ConnListener connListener) {
        super(context, connListener);
        this.device = device;
    }

    public ConnBLEDevice(Context context, BluetoothDevice device, ConnListener connListener, String readUUid, String writeUUid) {
        super(context, connListener);
        this.readUUid = readUUid;
        this.writeUUid = writeUUid;
        this.device = device;
    }

    public void setReadUUid(String readUUid) {
        this.readUUid = readUUid;
    }

    public void setWriteUUid(String writeUUid) {
        this.writeUUid = writeUUid;
    }

    @Override
    public boolean deviceCanUse() {
        //设备不支持ble蓝牙
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    /**
     * 连接设备
     */
    @Override
    public void connect() {
        if (device == null) {
            if (mConnListener != null){
                mConnListener.connException("没有要连接的设备!");
                mConnListener = null;
            }
            return;
        }

        //----------------开始连接--------------------
        //初始化BluetoothGattCallBack
        if (mConnListener != null){
            mConnListener.startConnDevice();
        }
        initBluetoothGattCallBack();

        mBluetoothGatt = device.connectGatt(context, false, mBluetoothGattCallBack);
    }


    /**
     * 初始化BluetoothGattCallBack
     */
    private void initBluetoothGattCallBack() {
        if (mBluetoothGattCallBack != null) {
            return;
        }
        mBluetoothGattCallBack = new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                super.onConnectionStateChange(gatt, status, newState);

                isConning = true;

                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    mBluetoothGatt.discoverServices();
                } else  {
                    disconnect();
                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                super.onServicesDiscovered(gatt, status);
                if(readUUid == null || readUUid.isEmpty() || writeUUid == null || writeUUid.isEmpty()){
                    for (UUIDEnum uuid: UUIDEnum.values()) {
                        readUUid = uuid.getReadUUID();
                        writeUUid = uuid.getWriteUUID();
                        if(getVisitGatt(mBluetoothGatt.getServices())){
                            return;
                        }
                    }
                    if(mConnListener != null){
                        mConnListener.connException("无法连接该设备！");
                        mConnListener = null;
                    }
                }else{
                    getVisitGatt(mBluetoothGatt.getServices());
                }
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                super.onCharacteristicChanged(gatt, characteristic);
                revMsg(characteristic.getValue());
            }

            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicWrite(gatt, characteristic, status);
            }
        };
    }

    /**
     * 得到和北斗的通信模块
     * 这里可以比作到一个学校里去找同学，而通信模块所需的读写模块就可以看做俩个同学，
     * readUUid，writeUUid就是我们要找的俩个同学的身份证号码。
     * 把mBluetoothGatt比作学校，BluetoothGattService比作班级
     * BluetoothGattCharacteristic就可以看做同学。
     *
     * @param services  连接
     * @return 连接成功或设失败
     */
    private boolean getVisitGatt(List<BluetoothGattService> services ) {
        if (services == null || services.size() == 0) {
            return false;
        }

        readCharacteristic = null;
        writeCharacteristic = null;

        //遍历学校里每一个班级
        for (BluetoothGattService gattService : services) {
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
            //遍历班级里每一个同学
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                String uuid = gattCharacteristic.getUuid().toString();
                //得到同学的身份证号码进行比对
                if (uuid.equals(readUUid)) {
                    readCharacteristic = gattCharacteristic;
                }
                if (uuid.equals(writeUUid)) {
                    writeCharacteristic = gattCharacteristic;
                }

                //找到了就不在找了
                if (readCharacteristic != null && writeCharacteristic != null) {
                    mBluetoothGatt.setCharacteristicNotification(readCharacteristic, true);
                    isConning = true;
                    if (mConnListener != null){
                        mConnListener.connSuccess();
                    }
                    Log.i(TAG, "连接成功；readUUID: " + readUUid + ", writeUUID: " + writeUUid);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 断开连接
     * @return 断开状态
     */
    @Override
    public boolean disconnect() {
        if(!isConning){
            return false;
        }
        isConning = false;
        readCharacteristic = null;
        writeCharacteristic = null;
        if (mBluetoothGatt != null) {
            try {
                if (mConnListener != null){
                    mConnListener.connBreak();
                    mConnListener = null;
                }
                mBluetoothGatt.disconnect();
                mBluetoothGatt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mBluetoothGatt = null;

        if(bleHandler != null){
            bleHandler.removeCallbacksAndMessages(0);
            bleHandler = null;
            bleHandlerThread.quit();
            bleHandlerThread = null;
        }
        return true;
    }

    /**
     * 发送蓝牙信息。设备--》蓝牙
     */
    @Override
    public void sendMsg(String msg) {

        if (writeCharacteristic != null && mBluetoothGatt != null) {
            sendMsgFromHandler(msg + "\r\n");
        }
    }

    private HandlerThread bleHandlerThread;
    private Handler bleHandler;

    private void sendMsgFromHandler(String msg){
        if(bleHandler == null){
            bleHandlerThread = new HandlerThread("发送ble蓝牙");
            bleHandlerThread.start();
            bleHandler = new Handler(bleHandlerThread.getLooper(), new Handler.Callback() {
                @Override
                public boolean handleMessage(Message message) {
                    sendCmd((byte[]) message.obj);
                    return true;
                }
            });
        }
        bleHandler.sendMessage(bleHandler.obtainMessage(0, msg.getBytes()));

    }


    private synchronized void sendCmd(byte[] bytes){
        byte[] newbytes = new byte[19];
        int index = 0;
        while (true){
            if(bytes.length > index*19+19){
                System.arraycopy(bytes, index*19, newbytes, 0, 19);
                index++;
            }else{
                System.arraycopy(bytes, index*19, newbytes, 0, bytes.length - index*19);
                index = -1;
            }

            writeCharacteristic.setValue(newbytes);
            writeCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
            mBluetoothGatt.writeCharacteristic(writeCharacteristic);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(index == -1){
                break;
            }
        }
    }

}
