package com.xiangshui.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public abstract class MapOptions<P, T> extends HashMap<P, T> {
    public MapOptions(List<T> data) {
        if (data != null && data.size() > 0) {
            data.forEach(t -> {
                if (t != null) {
                    P p = this.getPrimary(t);
                    if (p != null) {
                        this.put(p, t);
                    }
                }
            });
        }
    }

    public abstract P getPrimary(T t);

    public List<T> selectByPrimarys(Set<P> primarySet) {
        List<T> list = new ArrayList<>();
        if (primarySet != null && primarySet.size() > 0) {
            for (P p : primarySet) {
                T t = this.get(p);
                if (t != null) {
                    list.add(t);
                }
            }
        }
        return list;
    }

    public <R> R tryValueForResult(P key, CallBackForResult<T, R> callBackForResult, R defaultValue) {
        if (!this.containsKey(key)) {
            return defaultValue;
        }
        T value = this.get(key);
        if (value == null) {
            return defaultValue;
        }
        if (callBackForResult == null) {
            return defaultValue;
        }
        return callBackForResult.run(value);
    }


}
