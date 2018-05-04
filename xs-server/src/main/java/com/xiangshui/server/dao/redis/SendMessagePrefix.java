package com.xiangshui.server.dao.redis;

public class SendMessagePrefix extends KeyPrefix {
    public SendMessagePrefix(String prefix, int expiry) {
        super(prefix, expiry);
    }

    public static final KeyPrefix cache = new SendMessagePrefix("cache", 1000 * 60 * 60);

}
