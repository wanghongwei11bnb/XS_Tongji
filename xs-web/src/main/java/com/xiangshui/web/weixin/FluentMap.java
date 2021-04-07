package com.xiangshui.web.weixin;

import java.util.TreeMap;

public class FluentMap extends TreeMap<String, String> {
    public FluentMap fluentPut(String key, String value) {
        super.put(key, value);
        return this;
    }
}
