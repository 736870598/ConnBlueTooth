package com.sunxiaoyu.connbtcore.dev;

import android.content.Context;

import com.sunxiaoyu.connbtcore.listener.ConnListener;
import com.sunxiaoyu.connbtcore.utils.BtDataUtils;


/**
 * ConnDev 连接设备基类
 *
 * @author wzz created at 2017/2/8 13:43
 */
public abstract class ConnDev{

    protected Context context;
    protected ConnListener mConnListener;

    public ConnDev(Context context, ConnListener connListener) {
        this.context = context.getApplicationContext();
        mConnListener = connListener;
    }

    /**
     * 设备是否可用
     * 返回false表示设备不可用，true表示打开设备成功
     */
    public abstract boolean deviceCanUse();

    /**
     * 连接设备
     */
    public abstract void connect();

    /**
     * 断开设备连接
     */
    public boolean disconnect(){
        cancelConnListener();
        return true;
    }

    public void cancelConnListener(){
        mConnListener = null;
    }

    /**
     * 发送消息
     */
    public abstract void sendMsg(String msg);

    /**
     * 收到消息
     * @param buffer
     */
    public void revMsg(byte[] buffer){
        revMsg(buffer, buffer.length);
    }

    public void revMsg(byte[] buffer, int size){
        if (mConnListener != null){
            try {
                mConnListener.revMsg(buffer, size);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}
