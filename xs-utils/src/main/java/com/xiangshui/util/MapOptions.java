package com.xiangshui.util;

import java.util.HashMap;
import java.util.List;

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
}
