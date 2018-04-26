package com.xiangshui.util.service;

public class ServiceResult<T> {
    public int status;
    public String msg;
    public T result;

    public ServiceResult(int status, String msg, T result) {
        this.status = status;
        this.msg = msg;
        this.result = result;
    }
}
