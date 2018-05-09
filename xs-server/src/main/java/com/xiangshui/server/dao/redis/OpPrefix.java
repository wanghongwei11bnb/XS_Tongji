package com.xiangshui.server.dao.redis;

public class OpPrefix extends KeyPrefix {

    public OpPrefix(String prefix) {
        super(prefix);
    }

    public OpPrefix(String prefix, int expiry) {
        super(prefix, expiry);
    }

    public static final KeyPrefix op_token = new OpPrefix("op_token", 60 * 60 * 24);
}
