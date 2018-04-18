package com.sunxiaoyu.connbtcore.utils;


import java.util.Locale;

/**
 * 处理北斗数据转换的工具类
 * Created by sunxiaoyu on 2017/1/19.
 */
public class BtDataUtils {

    public static final String GB2312 = "GBK";
    public static final String UTF = "utf-8";

    /**
     *  Hex -> Byte
     */
    public static byte[] hex2Byte(String hex) throws Exception {
        hex = hex.trim().replace(" ", "").toUpperCase(Locale.US);
        byte[] baKeyword = new byte[hex.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            baKeyword[i] = (byte) (0xFF & Integer.parseInt(hex.substring(i * 2, i * 2 + 2), 16));
        }
        return baKeyword;
    }

    /**
     * Byte -> Hex
     * 转后的Hex为大写字符。长度为偶数
     */
    public static String byte2Hex(byte[] bytes, int count) throws Exception {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() % 2 == 1) {
                sb.append("0");
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * String -> Hex
     */
    public static String string2Hex(String str) {
        try {
            byte[] bytes = str.getBytes(BtDataUtils.GB2312);
            StringBuilder sb = new StringBuilder(bytes.length * 2);
            for (int i = 0; i < bytes.length; i++) {
                sb.append("0123456789ABCDEF".charAt((bytes[i] & 0XF0) >> 4));
                sb.append("0123456789ABCDEF".charAt((bytes[i] & 0X0F)));
            }
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * string -- hex
     * @param str          要转换的str
     * @param byteLength   目标字节数
     */
    public static String string2Hex( String str, int byteLength){
        String hex = (BtDataUtils.string2Hex(str));
        int length = byteLength * 2 - hex.length();
        for (int i = 0; i < length; i+=2){
            hex = "30" + hex;
        }
        return hex;
    }

    /**
     * Hex -> int
     */
    public static int hex2Integer(String hex) {
        return Integer.parseInt(hex, 16);
    }

    /**
     * Hex -> String
     */
    public static String hex2String(String str) {
        try {
            return new String(hex2Byte(str), GB2312);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Hex -> String
     */
    public static String hex2String(String str, String charSet) {
        try {
            return new String(hex2Byte(str), charSet);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * int -> Hex
     */
    public static String integer2Hex(int integer, int length) {
        try {
            String Hex = Integer.toHexString(integer).toUpperCase();
            int hexLen = Hex.length();
            if (length > hexLen) {
                hexLen = (length - hexLen);
                for (int i = 0; i < hexLen; i++) {
                    Hex = "0" + Hex;
                }
            }
            return Hex;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 计算校验和
     */
    public static String doCheckSum(String msg) {
        byte cmdBytes[] = null;
        int i;
        byte bt;
        try {
            cmdBytes = hex2Byte(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (cmdBytes == null || cmdBytes.length == 0){
            return "";
        }
        for (bt = cmdBytes[0], i = 1; i < cmdBytes.length; i++) {
            bt ^= cmdBytes[i];
        }

        String hex = Integer.toHexString(bt & 0xFF);
        if (hex.length() == 1) {
            hex = '0' + hex;
        }
        return hex.toUpperCase();
    }

    /**
     * str --> int
     * @param str  要转的string
     * @param def  默认值
     * @return  返回转换后的值
     */
    public static int string2Int(String str, int def){
        if(str == null || str.isEmpty()){
            return def;
        }
        try {
            return Integer.parseInt(str);
        }catch (Exception e){
            return def;
        }
    }

    /**
     * hex -- 字节
     * @return  返回转换后的值
     */
    public static String hex2Binary(String hexStr){
        String binary = Integer.toBinaryString(Integer.valueOf(hexStr,16));
        int count = hexStr.length()*4;
        count = count - binary.length();
        for (int i = 0; i < count; i++) {
            binary = "0" + binary;
        }
        return binary;
    }


}
