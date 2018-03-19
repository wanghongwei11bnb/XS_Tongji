package com.xiangshui.tj.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

abstract public class DataManager<K, V> {
    private static final Logger log = LoggerFactory.getLogger(DataManager.class);

    private Map<K, V> map = new HashMap<>();

    public V getById(K id) {
        return map.get(id);
    }

    public boolean exists(K id) {
        return map.containsKey(id);
    }

    public boolean save(V v) {
        if (v == null) {
            return false;
        }
        K k = getId(v);
        if (k == null) {
            return false;
        }
        map.put(k, v);
        return true;
    }

    public boolean removeById(K k) {
        if (k == null) {
            return false;
        }
        map.remove(k);
        return true;
    }

    public boolean remove(V v) {
        if (v == null) {
            return false;
        }
        K k = getId(v);
        if (k == null) {
            return false;
        }
        map.remove(k);
        return true;
    }

    abstract K getId(V v);

}
