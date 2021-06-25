package com.xiangshui.server.domain;

import lombok.Data;

@Data
public class ActivityPush {
    private Long push_id;
    private Long create_time;
    private Long push_time;
    private Long status;
    private Long type;


    private String content;
    private String content_img;
    private String contentId;
    private String img_url;
    private String text;
    private String title;


}
