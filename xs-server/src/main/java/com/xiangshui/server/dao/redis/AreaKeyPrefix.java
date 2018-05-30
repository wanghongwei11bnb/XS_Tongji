package com.xiangshui.server.dao.redis;

public class AreaKeyPrefix extends KeyPrefix {

    public AreaKeyPrefix(String prefix) {
        super(prefix);
    }

    public AreaKeyPrefix(String prefix, int expiry) {
        super(prefix, expiry);
    }

    public static final KeyPrefix cache = new AreaKeyPrefix("cache", 60);
}
