package com.sunxiaoyu.connbtcore.listener;

import android.bluetooth.BluetoothDevice;


import com.sunxiaoyu.connbtcore.bluetooth.SearchRequest;

import java.util.Set;

/**
 * 蓝牙管理者接口
 * Created by sunxiaoyu on 2017/2/9.
 */
public interface BTManagerListener {
    boolean devCanUseBlueTooth();
    boolean isBlueToothOpen();
    void openBlueTooth();
    void closeBlueTooth();
    void startSearch(SearchRequest searchRequest, BTSearchListener searchListener);
    void stopSearch();
    boolean connBleDev(BluetoothDevice device, String readUUID, String writeUUID);
    boolean connClassicsDev(BluetoothDevice device);
    boolean writeToDev(String msg);
    Set<BluetoothDevice> getBondedDevices();
}
