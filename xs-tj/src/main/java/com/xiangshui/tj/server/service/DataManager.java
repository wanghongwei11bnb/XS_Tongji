package com.xiangshui.tj.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.function.BiConsumer;

abstract public class DataManager<K, V> {
    private static final Logger log = LoggerFactory.getLogger(DataManager.class);

    private volatile Map<K, V> map = new HashMap<>();

    public synchronized V getById(K id) {
        return map.get(id);
    }

    public synchronized boolean exists(K id) {
        return map.containsKey(id);
    }

    public synchronized boolean save(V v) {
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

    public synchronized boolean removeById(K k) {
        if (k == null) {
            return false;
        }
        map.remove(k);
        return true;
    }

    public synchronized boolean remove(V v) {
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

    public synchronized int size() {
        return map.size();
    }

    public synchronized void foreach(BiConsumer<K, V> consumer) {
        map.forEach(consumer);
    }

    public synchronized Map<K, V> getMap() {
        return map;
    }
}
