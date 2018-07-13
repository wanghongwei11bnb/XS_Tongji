package com.xiangshui.server.domain.mysql;

import java.util.Date;

public class Article {

    private Integer id;
    private String title;
    private String subtitle;
    private String headimg;
    private String content;
    private Date release_time;
    private Integer status;

    public Integer getId() {
        return id;
    }

    public Article setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Article setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public Article setSubtitle(String subtitle) {
        this.subtitle = subtitle;
        return this;
    }

    public String getHeadimg() {
        return headimg;
    }

    public Article setHeadimg(String headimg) {
        this.headimg = headimg;
        return this;
    }

    public String getContent() {
        return content;
    }

    public Article setContent(String content) {
        this.content = content;
        return this;
    }

    public Date getRelease_time() {
        return release_time;
    }

    public Article setRelease_time(Date release_time) {
        this.release_time = release_time;
        return this;
    }

    public Integer getStatus() {
        return status;
    }

    public Article setStatus(Integer status) {
        this.status = status;
        return this;
    }
}
