package com.xiangshui.server.domain.mysql;

import lombok.Data;

import java.util.Date;
@Data
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

}
