package com.xiangshui.server.dao.redis;

public class OpPrefix extends KeyPrefix {

    public OpPrefix(String prefix) {
        super(prefix);
    }

    public OpPrefix(String prefix, int expiry) {
        super(prefix, expiry);
    }

    public static final KeyPrefix op_token = new OpPrefix("op_token", 60 * 60 * 24);
    public static final KeyPrefix cache = new OpPrefix("cache", 60 * 5);
    public static final KeyPrefix session = new OpPrefix("session", 60 * 60 * 24 * 7);
    public static final KeyPrefix auth_set = new OpPrefix("auth_list", 60 * 60);
    public static final KeyPrefix city_set = new OpPrefix("city_list", 60 * 60);
    public static final KeyPrefix area_set = new OpPrefix("area_list", 60 * 60);
}
