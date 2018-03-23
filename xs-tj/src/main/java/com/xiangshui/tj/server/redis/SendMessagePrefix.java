package com.xiangshui.tj.server.redis;

public class SendMessagePrefix extends KeyPrefix {

    public static boolean debug;

    public SendMessagePrefix(String prefix) {
        super(prefix);
    }

    public static final KeyPrefix cache = new SendMessagePrefix("cache");

    @Override
    public String getRealKey(String key) {
        return this.getClass().getSimpleName() + ":" + (debug ? "" : "online_") + this.prefix + ":" + key;
    }
}
