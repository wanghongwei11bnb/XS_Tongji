package com.xiangshui.server.domain.mysql;

import lombok.Data;

@Data
public class PrizeQuota {
    private Integer id;
    private Integer act_id;
    private Integer create_time;
    private Integer update_time;
    private Integer expires_time;
    private Integer receive_time;
    private Integer status;
    private Integer receive_status;
    private String prize_type;
    private String prize_title;
    private String prize_sub_title;
    private String prize_text;
    private String prize_text_small;
}
