package com.sunxiaoyu.connbtcore.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;

import com.sunxiaoyu.connbtcore.listener.BTSearchListener;


/**
 * 扫描蓝牙设备
 * Created by sunxiaoyu on 2017/2/9.
 */
public class StartSearchBlueToothDev {

    private Context context;
    private BluetoothAdapter bluetoothAdapter;
    private SearchRequest searchRequest;
    private BTSearchListener searchListener;

    private BluetoothAdapter.LeScanCallback leScanCallback;
    private ScanCallback scanCallback;
    private BroadcastReceiver broadcastReceiver;
    private IntentFilter intentFilter;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    stopClassicsSearch();
                    break;
                case 1:
                    stopBleSearch();
                    break;
                case 2:
                    stopSearch();
                    break;
            }
            return false;
        }
    });

    public StartSearchBlueToothDev(Context context, BluetoothAdapter bluetoothAdapter, SearchRequest searchRequest, BTSearchListener listener) {
        this.context = context;
        this.bluetoothAdapter = bluetoothAdapter;
        this.searchRequest = searchRequest;
        this.searchListener = listener;
    }

    public void setSearchRequest(SearchRequest searchRequest) {
        this.searchRequest = searchRequest;
    }

    public void setListener(BTSearchListener listener) {
        this.searchListener = listener;
    }

    public void setBluetoothAdapter(BluetoothAdapter bluetoothAdapter) {
        this.bluetoothAdapter = bluetoothAdapter;
    }

    public void stopClassicsSearch(){
        unSginBroadcastReceiver();
        bluetoothAdapter.cancelDiscovery();
        handler.removeMessages(0);
    }

    public void stopBleSearch(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if(scanCallback != null &&  bluetoothAdapter.getBluetoothLeScanner() != null)
                bluetoothAdapter.getBluetoothLeScanner().stopScan(scanCallback);
        }else{
            if(leScanCallback != null)
                bluetoothAdapter.stopLeScan(leScanCallback);
        }
        handler.removeMessages(1);

    }

    public void stopSearch(){
        if(searchListener != null)
            searchListener.stopSearch();

        handler.removeMessages(2);
        handler.removeCallbacksAndMessages(0);
        searchListener = null;
        searchRequest = null;
    }

    public void startSearch(){
        if(searchListener != null)
            searchListener.startSearch();

        handler.removeMessages(2);
        if(searchRequest != null){
            if(searchRequest.getSearchClassicsDevTime() > 0){
                sginBroadcastReceiver();
                bluetoothAdapter.startDiscovery();
                handler.sendEmptyMessageDelayed(0, searchRequest.getSearchClassicsDevTime());
            }
            if(context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE) && searchRequest.getSearchBleDevTime() > 0){
                initleScanCallback();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    bluetoothAdapter.getBluetoothLeScanner().startScan(scanCallback);
                }else{
                    bluetoothAdapter.startLeScan(leScanCallback);
                }

                handler.sendEmptyMessageDelayed(1, searchRequest.getSearchBleDevTime());
            }
            handler.sendEmptyMessageDelayed(2, Math.max(searchRequest.getSearchBleDevTime(),
                    searchRequest.getSearchClassicsDevTime())+100);
        }else{
            if(searchListener != null)
                searchListener.stopSearch();
        }
    }



    private void initleScanCallback(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if(scanCallback == null){
                scanCallback = new ScanCallback() {
                    @Override
                    public void onScanResult(int callbackType, ScanResult result) {
                        super.onScanResult(callbackType, result);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && searchListener != null && result != null) {
                            searchListener.findDevice(result.getDevice());
                        }
                    }
                };
            }
        }else{
            if(leScanCallback == null){
                leScanCallback = new BluetoothAdapter.LeScanCallback() {
                    @Override
                    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                        if (searchListener != null && device != null)
                            searchListener.findDevice(device);
                    }
                };
            }
        }
    }

    /**
     * 注册广播（监听现设备 连接断开 状态 ）
     */
    private void sginBroadcastReceiver(){
        if(broadcastReceiver == null){
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                        // 发现设备
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        if(device != null)
                            searchListener.findDevice(device);
                    }
                }
            };
            intentFilter = new IntentFilter();
            intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        }
        if (context != null && broadcastReceiver != null && intentFilter != null)
            context.registerReceiver(broadcastReceiver, intentFilter);
    }

    /**
     * 注销广播
     */
    private void unSginBroadcastReceiver(){
        try {
            if (context != null && broadcastReceiver != null)
                context.unregisterReceiver(broadcastReceiver);
        }catch (Exception e){
            e.printStackTrace();
        }
    }



}
