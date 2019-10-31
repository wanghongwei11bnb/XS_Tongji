package com.xiangshui.server.domain;

import java.util.Date;

public class SwiperItem {
    private Integer id;
    private String title;
    private String sub_title;
    private String img;
    private String link;
    private Integer status;
    private Date create_time;
    private Integer sort_num;
    private String app;

    public Integer getId() {
        return id;
    }

    public SwiperItem setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public SwiperItem setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getSub_title() {
        return sub_title;
    }

    public SwiperItem setSub_title(String sub_title) {
        this.sub_title = sub_title;
        return this;
    }

    public String getImg() {
        return img;
    }

    public SwiperItem setImg(String img) {
        this.img = img;
        return this;
    }

    public String getLink() {
        return link;
    }

    public SwiperItem setLink(String link) {
        this.link = link;
        return this;
    }

    public Integer getStatus() {
        return status;
    }

    public SwiperItem setStatus(Integer status) {
        this.status = status;
        return this;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public SwiperItem setCreate_time(Date create_time) {
        this.create_time = create_time;
        return this;
    }

    public Integer getSort_num() {
        return sort_num;
    }

    public SwiperItem setSort_num(Integer sort_num) {
        this.sort_num = sort_num;
        return this;
    }

    public String getApp() {
        return app;
    }

    public SwiperItem setApp(String app) {
        this.app = app;
        return this;
    }
}
