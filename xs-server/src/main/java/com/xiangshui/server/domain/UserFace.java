package com.xiangshui.server.domain;


import java.util.Map;

public class UserFace {
    private Integer uin;
    private String image_url;
    private String face_token;
    private Long create_time;
    private Map analyze_result;

    public Integer getUin() {
        return uin;
    }

    public void setUin(Integer uin) {
        this.uin = uin;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getFace_token() {
        return face_token;
    }

    public void setFace_token(String face_token) {
        this.face_token = face_token;
    }

    public Long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Long create_time) {
        this.create_time = create_time;
    }

    public Map getAnalyze_result() {
        return analyze_result;
    }

    public void setAnalyze_result(Map analyze_result) {
        this.analyze_result = analyze_result;
    }
}
