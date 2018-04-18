package com.sunxiaoyu.connbtcore.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;

import com.sunxiaoyu.connbtcore.listener.BTSearchListener;
import com.sunxiaoyu.connbtcore.listener.BTManagerListener;

import java.util.Set;


/**
 * 蓝牙管理器
 * Created by sunxiaoyu on 2017/2/9.
 */
public class BlueManager implements BTManagerListener {
    private Context context;
    private BluetoothAdapter bluetoothAdapter;
    private StartSearchBlueToothDev startSearchBlueToothDev;

    public BlueManager(Context context) {
        this.context = context;
    }

    @Override
    public boolean devCanUseBlueTooth() {
        if(bluetoothAdapter == null){
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        return (bluetoothAdapter != null);
    }

    @Override
    public boolean isBlueToothOpen() {
        return devCanUseBlueTooth() && bluetoothAdapter.isEnabled();
    }

    @Override
    public void openBlueTooth() {
        if(devCanUseBlueTooth() && !bluetoothAdapter.isEnabled())
            bluetoothAdapter.enable();
    }

    @Override
    public Set<BluetoothDevice> getBondedDevices(){
        if(devCanUseBlueTooth() && bluetoothAdapter.isEnabled())
            return bluetoothAdapter.getBondedDevices();
        return null;
    }

    @Override
    public void closeBlueTooth() {
        if(devCanUseBlueTooth() && bluetoothAdapter.isEnabled())
            bluetoothAdapter.disable();
    }

    @Override
    public void startSearch(SearchRequest searchRequest, BTSearchListener searchListener) {
        if(devCanUseBlueTooth()){
            openBlueTooth();
            if(startSearchBlueToothDev == null){
                startSearchBlueToothDev = new StartSearchBlueToothDev(context, bluetoothAdapter, searchRequest, searchListener);
            }else{
                startSearchBlueToothDev.setSearchRequest(searchRequest);
                startSearchBlueToothDev.setListener(searchListener);
            }
            startSearchBlueToothDev.startSearch();
        }else{
            if(searchListener != null)
                searchListener.cancelSearch();
        }
    }

    @Override
    public void stopSearch() {
        if (startSearchBlueToothDev != null){
            startSearchBlueToothDev.stopBleSearch();
            startSearchBlueToothDev.stopClassicsSearch();
            startSearchBlueToothDev.stopSearch();
        }
    }

    @Override
    public boolean connBleDev(BluetoothDevice device, String readUUID, String writeUUID) {
        return false;
    }

    @Override
    public boolean connClassicsDev(BluetoothDevice device) {
        return false;
    }

    @Override
    public boolean writeToDev(String msg) {
        return false;
    }
}
