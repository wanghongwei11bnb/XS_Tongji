package com.xiangshui.server.domain.mysql;

import lombok.Data;

@Data
public class HomeMedia {
    private Integer id;
    private String title;
    private String sub_title;
    private String img_url;
    private String link_url;
    private Integer serial;

}
