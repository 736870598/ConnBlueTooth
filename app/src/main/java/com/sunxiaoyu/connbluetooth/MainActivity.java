package com.sunxiaoyu.connbluetooth;

import android.bluetooth.BluetoothDevice;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.sunxiaoyu.connbtcore.ConnBtManager;
import com.sunxiaoyu.connbtcore.listener.BTSearchListener;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ConnBtManager.api().init(this);

    }

    public void openBt(View view){
        if (!ConnBtManager.api().isBlueToothOpen()){
            ConnBtManager.api().openBlueTooth();
        }
    }

    public void searchBt(View view){
        ConnBtManager.api().search(0, 30 * 1000, new BTSearchListener() {
            @Override
            public void startSearch() {
                Log.v("sunxy", "startSearch");
            }

            @Override
            public void stopSearch() {
                Log.v("sunxy", "stopSearch");
            }

            @Override
            public void cancelSearch() {
                Log.v("sunxy", "cancelSearch");
            }

            @Override
            public void findDevice(BluetoothDevice device) {
                Log.v("sunxy", "findDevice--" + device.getAddress());
            }
        });
    }
}
