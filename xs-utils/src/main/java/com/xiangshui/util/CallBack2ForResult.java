package com.xiangshui.util;

public interface CallBack2ForResult<T1, T2, R> {
    R run(T1 t1, T2 t2);
}
