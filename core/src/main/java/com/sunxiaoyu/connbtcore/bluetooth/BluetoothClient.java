package com.sunxiaoyu.connbtcore.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;


import com.sunxiaoyu.connbtcore.listener.BTSearchListener;

import java.util.Set;

/**
 * 用户统一调用该类操作蓝牙相关
 * Created by sunxiaoyu on 2017/2/9.
 */
public class BluetoothClient {

    private static BluetoothClient instense;
    private BlueManager blueManager;

    public static BluetoothClient getInstense(Context context){
        if(instense == null){
            instense = new BluetoothClient(context);
        }
        return instense;
    }

    private BluetoothClient(Context context){
        blueManager = new BlueManager(context);
    }


    public boolean isBlueToothOpen(){
        return blueManager.isBlueToothOpen();
    }

    public boolean devCanUseBlueTooth(){
        return blueManager.devCanUseBlueTooth();
    }

    public void openBlueTooth(){
        blueManager.openBlueTooth();
    }

    public Set<BluetoothDevice> getBondedDevices(){
        return blueManager.getBondedDevices();
    }

    public void closeBlueTooth(){
        blueManager.closeBlueTooth();
    }

    public void search(SearchRequest searchRequest, BTSearchListener listener){
        blueManager.startSearch(searchRequest, listener);
    }

    public void stopSearch(){
        blueManager.stopSearch();
    }

    public boolean connClassicsDev(BluetoothDevice device){
        blueManager.stopSearch();
        return blueManager.connClassicsDev(device);
    }

    public boolean connBleDev(BluetoothDevice device, String readUUID, String writeUUID){
        blueManager.stopSearch();
        return blueManager.connBleDev(device, readUUID, writeUUID);
    }

    public boolean writeToDev(String msg){
        return blueManager.writeToDev(msg);
    }


}
