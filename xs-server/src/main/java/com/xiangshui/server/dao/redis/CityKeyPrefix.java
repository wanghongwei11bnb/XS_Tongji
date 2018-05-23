package com.xiangshui.server.dao.redis;

public class CityKeyPrefix extends KeyPrefix {

    public CityKeyPrefix(String prefix) {
        super(prefix);
    }

    public CityKeyPrefix(String prefix, int expiry) {
        super(prefix, expiry);
    }

    public static final KeyPrefix list_all = new CityKeyPrefix("list_all", 60 * 5);
    public static final KeyPrefix list_active = new CityKeyPrefix("list_active", 60 * 5);
}
