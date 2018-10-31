package com.xiangshui.server.domain.mysql;

public class HomeMedia {
    private Integer id;
    private String title;
    private String sub_title;
    private String img_url;
    private String link_url;
    private Integer serial;

    public Integer getId() {
        return id;
    }

    public HomeMedia setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public HomeMedia setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getSub_title() {
        return sub_title;
    }

    public HomeMedia setSub_title(String sub_title) {
        this.sub_title = sub_title;
        return this;
    }

    public String getImg_url() {
        return img_url;
    }

    public HomeMedia setImg_url(String img_url) {
        this.img_url = img_url;
        return this;
    }

    public String getLink_url() {
        return link_url;
    }

    public HomeMedia setLink_url(String link_url) {
        this.link_url = link_url;
        return this;
    }

    public Integer getSerial() {
        return serial;
    }

    public HomeMedia setSerial(Integer serial) {
        this.serial = serial;
        return this;
    }
}
