package com.xiangshui.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;

public class MD5 {

    private static final Logger log = LoggerFactory.getLogger(MD5.class);

    private static final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();

    public static String getMD5(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(s.getBytes("utf-8"));
            return getMD5(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getMD5(byte[] bytes) {
        StringBuilder ret = new StringBuilder(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            ret.append(HEX_DIGITS[(bytes[i] >> 4) & 0x0f]);
            ret.append(HEX_DIGITS[bytes[i] & 0x0f]);
        }
        return ret.toString();
    }


    public static void main(String[] args) {
        log.debug(getMD5("1"));
    }

}
