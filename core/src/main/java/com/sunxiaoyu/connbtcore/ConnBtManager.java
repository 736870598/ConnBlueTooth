package com.sunxiaoyu.connbtcore;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Build;

import com.sunxiaoyu.connbtcore.bluetooth.BluetoothClient;
import com.sunxiaoyu.connbtcore.bluetooth.SearchRequest;
import com.sunxiaoyu.connbtcore.dev.ConnBLEDevice;
import com.sunxiaoyu.connbtcore.dev.ConnBTDevice;
import com.sunxiaoyu.connbtcore.dev.ConnDev;
import com.sunxiaoyu.connbtcore.listener.BTSearchListener;
import com.sunxiaoyu.connbtcore.listener.ConnListener;

import java.util.Set;

import static android.bluetooth.BluetoothDevice.DEVICE_TYPE_CLASSIC;
import static android.bluetooth.BluetoothDevice.DEVICE_TYPE_LE;

/**
 * 链接设备manager
 */
public class ConnBtManager {

    private static volatile ConnBtManager defaultInstance;
    private ConnDev connDev;//连接的设备
    private Context mContext;
    private ConnListener newListener;
    private ConnListener oldListener;

    private ConnBtManager() {
    }

    public void init(Context context) {
        mContext = context.getApplicationContext();
    }

    /**
     * 获得单例对象
     */
    public static ConnBtManager api() {
        if (defaultInstance == null) {
            synchronized (ConnBtManager.class) {
                if (defaultInstance == null) {
                    defaultInstance = new ConnBtManager();
                }
            }
        }
        return defaultInstance;
    }

    /**
     * 设备是否能用蓝牙
     * @return
     */
    public boolean canUseBlueTooth(){
        return BluetoothClient.getInstense(mContext).devCanUseBlueTooth();
    }

    /**
     * 蓝牙是否开启
     */
    public boolean isBlueToothOpen(){
        return BluetoothClient.getInstense(mContext).isBlueToothOpen();
    }

    /**
     * 开始扫描，如果发现有可连接的设备
     * @param after4   扫描 蓝牙4.0以前 设备的时间
     * @param before4  扫描 蓝牙4.0以后 设备的时间
     * @param listener 监听器
     */
    public void search(long after4, long before4, BTSearchListener listener) {
        SearchRequest searchRequest = new SearchRequest(after4, before4);
        BluetoothClient.getInstense(mContext).search(searchRequest, listener);
    }

    /**
     * 停止扫描
     */
    public void stopSearch(){
        BluetoothClient.getInstense(mContext).stopSearch();
    }

    /**
     * 打开蓝牙
     */
    public void openBlueTooth(){
        BluetoothClient.getInstense(mContext).openBlueTooth();
    }

    /**
     * 获取之前匹配过的蓝牙设备集合（只有4.0以前的传统蓝牙才会进行配对）
     */
    public Set<BluetoothDevice> getBondedDevices(){
        return BluetoothClient.getInstense(mContext).getBondedDevices();
    }

    /**
     * 连接蓝牙设备
     * @param device       连接对象
     * @param connListener 状态监听
     * @return  连接状态
     */
    public boolean connDevice( BluetoothDevice device, ConnListener connListener) {

        oldListener = connListener;
        initConnListener();

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2){
            ConnBTDevice btClassicDevice = new ConnBTDevice(mContext, device, newListener);
            btClassicDevice.connect();
            connDev = btClassicDevice;
            return true;
        }
        if (device.getType() == DEVICE_TYPE_LE) {
            ConnBLEDevice bleDevice = new ConnBLEDevice(mContext, device, newListener);
            bleDevice.connect();
            connDev = bleDevice;
            return true;
        } else if (device.getType() == DEVICE_TYPE_CLASSIC) {
            ConnBTDevice btClassicDevice = new ConnBTDevice(mContext, device, newListener);
            btClassicDevice.connect();
            connDev = btClassicDevice;
            return true;
        }else{
            if(connListener != null){
                oldListener.connException("无法连接该设备");
            }
        }
        return false;
    }

    /**
     * 连接设备
     * @param device            连接对象
     * @param rId               ble设备的读数据模块的uuid
     * @param wId               ble设备的写数据模块的uuid
     * @param connListener      连接监听
     * @return  连接状态
     */
    public boolean connDevice(BluetoothDevice device, String rId, String wId, ConnListener connListener) {

        oldListener = connListener;
        initConnListener();

        if (device.getType() == DEVICE_TYPE_LE) {
            ConnBLEDevice bleDevice = new ConnBLEDevice(mContext, device, newListener, rId, wId);
            bleDevice.connect();
            connDev = bleDevice;
            return true;
        }

        return false;
    }

    /**
     * 初始化连接接口
     */
    private void initConnListener(){
        if(newListener == null){
            //实现线程转换
            newListener = new ConnListener() {
                @Override
                public void startConnDevice() {
                    if(oldListener != null){
                        oldListener.startConnDevice();
                    }
                }

                @Override
                public void connSuccess() {
                    //连接成功，打开相应指令
                    if(oldListener != null){
                        oldListener.connSuccess();
                    }
                }

                @Override
                public void revMsg(byte[] msg, int size) {
                    if(oldListener != null){
                        oldListener.revMsg(msg, size);
                    }
                }

                @Override
                public void connException( String error) {
                    if(oldListener != null){
                        oldListener.connException(error);
                        oldListener = null;
                    }
                }

                @Override
                public void connBreak() {
                    oldListener.connBreak();
                    oldListener = null;
                }
            };
        }
    }

    /**
     * 取消连接监听
     */
    public void cancelConnListener(){
        oldListener = null;
    }

    /**
     * 获得连接设备
     */
    public ConnDev getConnDev() {
        return connDev;
    }

    /**
     * 像设备发送消息
     */
    public void sendMsg(String msg){
        if (connDev != null){
            connDev.sendMsg(msg);
        }
    }

    /**
     * 断开已连接的设备
     */
    public void disconnect(){
        //主动断开蓝牙，直接断开，不去通知
        newListener = null;
        oldListener = null;
        if(connDev != null){
            connDev.cancelConnListener();
            connDev.disconnect();
            connDev = null;
        }
    }
}
