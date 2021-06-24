package com.xiangshui.server.domain;

import lombok.Data;

@Data
public class Activity {
    private Long id;
    private String img_url;
    private String name;
    private Long open;
    private Long order;
    private Long type;
    private String url;
}
