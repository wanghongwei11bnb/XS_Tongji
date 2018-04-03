package com.xiangshui.tj.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

abstract public class DataManager<K, V> {
    private static final Logger log = LoggerFactory.getLogger(DataManager.class);

    private Map<K, V> map = new Hashtable();

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

    public int size() {
        return map.size();
    }

    public Map<K, V> getMap() {
        return map;
    }

    public void setMap(Map<K, V> map) {
        this.map = map;
    }
}
