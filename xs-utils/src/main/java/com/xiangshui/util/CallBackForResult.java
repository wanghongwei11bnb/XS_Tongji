package com.xiangshui.util;

public interface CallBackForResult<T, R> {
    R run(T t);
}
