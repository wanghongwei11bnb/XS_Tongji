package com.xiangshui.tj.server.redis;

abstract public class KeyPrefix {
    public final String prefix;
    public final int expiry;

    public KeyPrefix(String prefix) {
        this(prefix, 0);
    }

    public KeyPrefix(String prefix, int expiry) {
        this.prefix = prefix;
        this.expiry = expiry;
    }

    public String getRealKey(String key) {
        return this.getClass().getSimpleName() + ":" + this.prefix + ":" + key;
    }

}
