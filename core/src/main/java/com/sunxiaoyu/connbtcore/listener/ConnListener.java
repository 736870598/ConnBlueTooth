package com.sunxiaoyu.connbtcore.listener;

/**
 * 设备连接状态接口
 * Created by sunxiaoyu on 2017/1/17.
 */
public interface ConnListener {

    /**
     *  STARTCONN
     */
    void startConnDevice();

    /**
     *  CONNECT
     */
    void connSuccess();

    /**
     *  EXCEPTION
     */
    void connException(String error);

    /**
     * 收到数据
     */
    void revMsg(byte[] msg, int size);

    /**
     *  CONNBREAK
     */
    void connBreak();


}
