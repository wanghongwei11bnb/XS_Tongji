package com.xiangshui.server.dao.redis;

public class TestKeyPrefix extends KeyPrefix {

    public TestKeyPrefix(String prefix) {
        super(prefix);
    }

    public TestKeyPrefix(String prefix, int expiry) {
        super(prefix, expiry);
    }

    public static final KeyPrefix test = new TestKeyPrefix("test", 1000 * 60 * 1);

}
