package com.xiangshui.util;

import java.util.TreeMap;

public class FluentTreeMap extends TreeMap<String, String> {
    public FluentTreeMap fluentPut(String key, String value) {
        super.put(key, value);
        return this;
    }
}
