package com.xiangshui.op.bean;

import java.util.Set;

public class WebMenu {
    private String title;
    private String path;
    private int sort;
    private Set<String> auths;

    public Set<String> getAuths() {
        return auths;
    }

    public WebMenu setAuths(Set<String> auths) {
        this.auths = auths;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }
}
