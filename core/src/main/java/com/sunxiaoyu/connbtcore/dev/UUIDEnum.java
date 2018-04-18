package com.sunxiaoyu.connbtcore.dev;

/**
 *
 * 查询所支持的ble蓝牙的读写uuid
 *
 * Created by sunxiaoyu on 2017/3/24.
 */
public enum UUIDEnum {

    GXBOX("0000ffe1-0000-1000-8000-00805f9b34fb", "0000ffe1-0000-1000-8000-00805f9b34fb"),
    QZBOX("0000ffe1-0000-1000-8000-00805f9b34fb", "0000ffe1-0000-1000-8000-00805f9b34fb"),
    XYXL ("0000fff4-0000-1000-8000-00805f9b34fb", "0000fff3-0000-1000-8000-00805f9b34fb");

    private String readUUID;
    private String writeUUID;

    UUIDEnum(String readUUID, String writeUUID){
        this.readUUID = readUUID;
        this.writeUUID = writeUUID;
    }

    public String getReadUUID() {
        return readUUID;
    }

    public String getWriteUUID() {
        return writeUUID;
    }
}
