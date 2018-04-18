package com.sunxiaoyu.connbtcore.listener;

import android.bluetooth.BluetoothDevice;

/**
 * 搜索蓝牙
 * Created by sunxiaoyu on 2017/2/9.
 */
public interface BTSearchListener {

    void startSearch();//搜索开始

    void stopSearch();//搜索停止

    void cancelSearch();//搜索取消

    void findDevice(BluetoothDevice device);//搜索到设备

}
