package com.xiangshui.util;

public interface CallBack3ForResult<T1, T2, T3, R> {
    R run(T1 t1, T2 t2, T3 t3);
}
