package com.xiangshui.util.service;

public class ServiceResult<T> {
    public int status;
    public String msg;
    public T result;

    public ServiceResult() {
    }

    public ServiceResult(int status) {
        this.status = status;
    }

    public ServiceResult(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public ServiceResult(int status, String msg, T result) {
        this.status = status;
        this.msg = msg;
        this.result = result;
    }

    public ServiceResult(T result) {
        this.result = result;
    }
}
