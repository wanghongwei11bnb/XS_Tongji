package com.xiangshui.server.domain.mysql;

import lombok.Data;

@Data
public class Transaction {
    private Long id;
    private Long create_time;
    private Long pay_time;
    private String subject;
    private Integer type;
    private Integer status;
    private Integer channel;
    private String transaction_id;
    private String out_trade_no;
    private String appid;
    private String mch_id;
    private String openid;
    private Integer total_fee;


    public enum Type {
        pc_buy_month_card(1, "PC端购买月卡"),
        pay_booking(2, "支付订单"),
        wallet_recharge(3, "钱包充值"),
        xcx_buy_month_card(4, "小程序端购买月卡"),
//
        ;

        public final Integer value;
        public final String text;

        Type(Integer value, String text) {
            this.value = value;
            this.text = text;
        }
    }


    public enum Status {
        normal(0, "待支付"),
        paid(1, "已支付"),
        expire(-1, "已过期"),
        stop(-500, "意外终止"),
//
        ;

        public final Integer value;
        public final String text;

        Status(Integer value, String text) {
            this.value = value;
            this.text = text;
        }
    }


    public enum Channel {
        wx(1, "微信"),
//
        ;

        public final Integer value;
        public final String text;

        Channel(Integer value, String text) {
            this.value = value;
            this.text = text;
        }
    }


}
