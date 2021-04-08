package com.xiangshui.server;

import com.xiangshui.util.MD5;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;


@Slf4j
public class IDGenerator {

    private static final long start = new LocalDate(2021, 1, 1).toDate().getTime();

    synchronized public static long generate() {
        return (long) ((System.currentTimeMillis() - start) * 1000 + Math.floor(Math.random() * 1000));
    }

    public static String generate(String... keys) {
        if (keys == null || keys.length == 0) {
            throw new RuntimeException("keys can not be null");
        }
        for (String key : keys) {
            if (StringUtils.isBlank(key)) {
                throw new RuntimeException("key can not be null");
            }
        }
        return MD5.getMD5(String.join(":", keys));
    }

    public static void main(String[] args) {
        log.info(String.valueOf(generate()));
        log.info(String.valueOf(Long.MAX_VALUE));
        log.info(MD5.getMD5("13501231224"));
        log.info(MD5.getMD5("1350123122").substring(11, 21));
    }

}
