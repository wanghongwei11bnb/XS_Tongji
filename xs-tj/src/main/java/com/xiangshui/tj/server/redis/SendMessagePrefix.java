package com.xiangshui.tj.server.redis;

public class SendMessagePrefix extends KeyPrefix {
    public SendMessagePrefix(String prefix) {
        super(prefix);
    }

    public static final KeyPrefix cache = new SendMessagePrefix("cache");
}
