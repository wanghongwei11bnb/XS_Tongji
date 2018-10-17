package com.xiangshui.server.domain.mysql;

import java.util.Date;

public class Article {

    private Integer id;
    private String title;
    private String author;
    private String sub_title;
    private String summary;
    private Integer category;
    private Integer sub_cate;
    private Integer type;
    private Date create_time;
    private Date update_time;
    private Integer status;
    private String head_img;
    private String remark;
    private String content;
    private Date release_time;

    public String getAuthor() {
        return author;
    }

    public Article setAuthor(String author) {
        this.author = author;
        return this;
    }

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

    public String getSub_title() {
        return sub_title;
    }

    public Article setSub_title(String sub_title) {
        this.sub_title = sub_title;
        return this;
    }

    public String getSummary() {
        return summary;
    }

    public Article setSummary(String summary) {
        this.summary = summary;
        return this;
    }

    public Integer getCategory() {
        return category;
    }

    public Article setCategory(Integer category) {
        this.category = category;
        return this;
    }

    public Integer getSub_cate() {
        return sub_cate;
    }

    public Article setSub_cate(Integer sub_cate) {
        this.sub_cate = sub_cate;
        return this;
    }

    public Integer getType() {
        return type;
    }

    public Article setType(Integer type) {
        this.type = type;
        return this;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public Article setCreate_time(Date create_time) {
        this.create_time = create_time;
        return this;
    }

    public Date getUpdate_time() {
        return update_time;
    }

    public Article setUpdate_time(Date update_time) {
        this.update_time = update_time;
        return this;
    }

    public Integer getStatus() {
        return status;
    }

    public Article setStatus(Integer status) {
        this.status = status;
        return this;
    }

    public String getHead_img() {
        return head_img;
    }

    public Article setHead_img(String head_img) {
        this.head_img = head_img;
        return this;
    }

    public String getRemark() {
        return remark;
    }

    public Article setRemark(String remark) {
        this.remark = remark;
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
}
