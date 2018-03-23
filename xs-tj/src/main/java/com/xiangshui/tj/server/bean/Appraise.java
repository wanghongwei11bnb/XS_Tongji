package com.xiangshui.tj.server.bean;

import java.util.List;

public class Appraise {

    private List<String> appraise;
    private int area_id;
    private long booking_id;
    private int createtime;
    private int score;
    private String suggest;


    private int uin;

    public String getSuggest() {
        return suggest;
    }

    public void setSuggest(String suggest) {
        this.suggest = suggest;
    }

    public List<String> getAppraise() {
        return appraise;
    }

    public void setAppraise(List<String> appraise) {
        this.appraise = appraise;
    }

    public int getArea_id() {
        return area_id;
    }

    public void setArea_id(int area_id) {
        this.area_id = area_id;
    }

    public long getBooking_id() {
        return booking_id;
    }

    public void setBooking_id(long booking_id) {
        this.booking_id = booking_id;
    }

    public int getCreatetime() {
        return createtime;
    }

    public void setCreatetime(int createtime) {
        this.createtime = createtime;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getUin() {
        return uin;
    }

    public void setUin(int uin) {
        this.uin = uin;
    }


    private String nick_name;
    private String phone;

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
