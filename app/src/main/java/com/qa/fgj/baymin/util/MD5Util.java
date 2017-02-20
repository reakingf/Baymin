package com.qa.fgj.baymin.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by FangGengjia on 2017/2/18.
 * MD5工具类
 */
public class MD5Util {

    private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F' };

    private static String byteArrayToHexString(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (byte aB : b) {
            sb.append(HEX_DIGITS[(aB & 0xf0) >>> 4]);
            sb.append(HEX_DIGITS[aB & 0x0f]);
        }
        return sb.toString();
    }

    public static String getMD5Digest(String s) {
        try {

            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            return byteArrayToHexString(messageDigest);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

}
