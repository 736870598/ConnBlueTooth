package com.sunxiaoyu.connbtcore.bluetooth;

/**
 * 设置扫描时间请求
 * Created by sunxiaoyu on 2017/2/9.
 */
public class SearchRequest {

    /**
     * 设置扫描经典蓝牙时间
     */
    private long searchClassicsDevTime;

    /**
     * 设置扫描低功耗蓝牙时间
     */
    private long searchBleDevTime;

    public SearchRequest() {}

    public SearchRequest(long searchClassicsDevTime, long searchBleDevTime) {
        this.searchClassicsDevTime = searchClassicsDevTime;
        this.searchBleDevTime = searchBleDevTime;
    }

    public long getSearchClassicsDevTime() {
        return searchClassicsDevTime;
    }


    public void setSearchClassicsDevTime(long searchClassicsDevTime) {
        this.searchClassicsDevTime = searchClassicsDevTime;
    }

    public long getSearchBleDevTime() {
        return searchBleDevTime;
    }

    public void setSearchBleDevTime(long searchBleDevTime) {
        this.searchBleDevTime = searchBleDevTime;
    }
}
