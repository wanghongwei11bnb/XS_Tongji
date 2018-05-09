package com.xiangshui.server.dao.redis;

public class CityKeyPrefix extends KeyPrefix {

    public CityKeyPrefix(String prefix) {
        super(prefix);
    }

    public CityKeyPrefix(String prefix, int expiry) {
        super(prefix, expiry);
    }

    public static final KeyPrefix cache = new CityKeyPrefix("cache", 60);
}
