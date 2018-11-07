package com.xiangshui.server.domain;

import com.xiangshui.server.domain.fragment.CapsuleType;
import com.xiangshui.server.domain.fragment.Location;
import com.xiangshui.server.domain.fragment.RushHour;

import java.util.List;

public class Area {

    private Integer area_id;

    private String address;
    private String city;
    /**
     * 百度定位图片url
     */
    private String area_img;
    private String title;
    /**
     * 提示文案
     */
    private String notification;
    /**
     * 联系方式
     */
    private String contact;
    /**
     * 场地状态
     * null:正常
     * -1:已下线
     * -2:待运营
     */
    private Integer status;

    private List<RushHour> rushHours;
    private Integer minute_start;

    //是否对外开放。0不开放，1开放
    private Integer is_external;

    //限时标记： 0 不限时，1 通用限时，2特殊限时
    private Integer is_time_limit;


    private Location location;

    private List<String> imgs;

    private List<CapsuleType> types;


    private Long create_time;


    private Integer need_deposit;


    private String remark;

    /**
     * 单笔订单时长限制（单位／分钟）
     * null 或 0 即不限制
     */
    private Integer use_time_limit;

    public Integer getUse_time_limit() {
        return use_time_limit;
    }

    public Area setUse_time_limit(Integer use_time_limit) {
        this.use_time_limit = use_time_limit;
        return this;
    }

    public String getRemark() {
        return remark;
    }

    public Area setRemark(String remark) {
        this.remark = remark;
        return this;
    }

    public Integer getNeed_deposit() {
        return need_deposit;
    }

    public Area setNeed_deposit(Integer need_deposit) {
        this.need_deposit = need_deposit;
        return this;
    }

    public Long getCreate_time() {
        return create_time;
    }

    public Area setCreate_time(Long create_time) {
        this.create_time = create_time;
        return this;
    }

    public Integer getIs_external() {
        return is_external;
    }

    public void setIs_external(Integer is_external) {
        this.is_external = is_external;
    }

    public Integer getArea_id() {
        return area_id;
    }

    public void setArea_id(Integer area_id) {
        this.area_id = area_id;
    }

    public Integer getIs_time_limit() {
        return is_time_limit;
    }

    public void setIs_time_limit(Integer is_time_limit) {
        this.is_time_limit = is_time_limit;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getArea_img() {
        return area_img;
    }

    public void setArea_img(String area_img) {
        this.area_img = area_img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<RushHour> getRushHours() {
        return rushHours;
    }

    public void setRushHours(List<RushHour> rushHours) {
        this.rushHours = rushHours;
    }

    public Integer getMinute_start() {
        return minute_start;
    }

    public void setMinute_start(Integer minute_start) {
        this.minute_start = minute_start;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public List<String> getImgs() {
        return imgs;
    }

    public void setImgs(List<String> imgs) {
        this.imgs = imgs;
    }

    public List<CapsuleType> getTypes() {
        return types;
    }

    public void setTypes(List<CapsuleType> types) {
        this.types = types;
    }
}
