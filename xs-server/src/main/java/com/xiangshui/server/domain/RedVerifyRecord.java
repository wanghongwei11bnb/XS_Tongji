package com.xiangshui.server.domain;

public class RedVerifyRecord {


    private Integer uin;
    private String verify_code;
    private Long time;
    private Integer red_envelope;

    public Integer getUin() {
        return uin;
    }

    public RedVerifyRecord setUin(Integer uin) {
        this.uin = uin;
        return this;
    }

    public String getVerify_code() {
        return verify_code;
    }

    public RedVerifyRecord setVerify_code(String verify_code) {
        this.verify_code = verify_code;
        return this;
    }

    public Long getTime() {
        return time;
    }

    public RedVerifyRecord setTime(Long time) {
        this.time = time;
        return this;
    }

    public Integer getRed_envelope() {
        return red_envelope;
    }

    public RedVerifyRecord setRed_envelope(Integer red_envelope) {
        this.red_envelope = red_envelope;
        return this;
    }
}
