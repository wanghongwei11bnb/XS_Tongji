package com.xiangshui.op.bean;

import java.util.Date;

public class OpRecord {

    private String username;
    private Date req_time;
    private String url;
    private String method;
    private String body;

    public String getUsername() {
        return username;
    }

    public OpRecord setUsername(String username) {
        this.username = username;
        return this;
    }

    public Date getReq_time() {
        return req_time;
    }

    public OpRecord setReq_time(Date req_time) {
        this.req_time = req_time;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public OpRecord setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getMethod() {
        return method;
    }

    public OpRecord setMethod(String method) {
        this.method = method;
        return this;
    }

    public String getBody() {
        return body;
    }

    public OpRecord setBody(String body) {
        this.body = body;
        return this;
    }
}
