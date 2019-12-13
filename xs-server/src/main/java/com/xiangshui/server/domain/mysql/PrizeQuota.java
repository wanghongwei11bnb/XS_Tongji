package com.xiangshui.server.domain.mysql;

import lombok.Data;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

@Data
public class PrizeQuota {
    private Integer id;
    private String activity;
    private Long create_time;
    private Long update_time;
    private Long expires_time;
    private Long receive_time;
    private Integer receive_status;
    private String prize_type;
    private Integer uin;
    private Long booking_id;
    private Integer price;
    private Integer min_price;

    public enum ActivityEnum {

        act_201912(null, "部分场地结束订单后提示10元红包发放申领弹窗", new DateTime(2019, 12, 31, 23, 59, 59, 999));

        public final String title;
        public final String remark;
        public final DateTime expire_time;

        ActivityEnum(String title, String remark, DateTime expire_time) {
            this.title = title;
            this.remark = remark;
            this.expire_time = expire_time;
        }
    }

    public enum PrizeTypeEnum {

        blank("blank", null),
        coupon("优惠券", null),
        balance("钱包余额", null),
        gift("礼品", null),;

        public final String title;
        public final String remark;

        PrizeTypeEnum(String title, String remark) {
            this.title = title;
            this.remark = remark;
        }
    }
}
