package com.xiangshui.server.domain.mysql.qingsu;

import lombok.Data;

@Data
public class PourBooking {
    private Long id;
    private Integer uin;
    private String phone;
    private String hear_phone;
    private Long start_time;
    private Long end_time;
    private Long talk_time;
    private Integer total_price;
    private Integer discount_price;
    private Integer final_price;
    private Integer pay_price;
    private Integer pay_type;
    private String pay_id;
    /**
     * 0:创建,1:进行中，2:待支付,3:已支付
     */
    private Integer status;
    private Integer star;
    private Long create_time;
    private String remark;
}
