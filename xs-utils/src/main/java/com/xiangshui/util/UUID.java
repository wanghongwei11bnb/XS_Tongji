package com.xiangshui.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UUID {
    public static final char[] cs = "0123456789abcdef".toCharArray();


    public static String get(char[] cs, int n) {
        StringBuilder stringBuilder = new StringBuilder();
        while (n-- > 0) {
            stringBuilder.append(cs[(int) (Math.random() * cs.length)]);
        }
        return stringBuilder.toString();
    }


    public static String get(int n) {
        return get(cs, n);
    }


    public static String get() {
        return get(cs, 32);
    }


    public static String getFromMd5(String md5) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= 8; i++) {
            String str = md5.substring((i - 1)*4, (i - 1)*4 + 4);
            log.info("{}-{}",str,Long.parseLong(str,16));


        }
        return sb.toString();
    }


    public static void main(String[] args) {
        String md5=MD5.getMD5("1");
        log.info(md5);
        log.info(getFromMd5(md5));
    }
}
