package com.xiangshui.server.domain;

import com.xiangshui.server.domain.fragment.CapsuleType;
import com.xiangshui.server.domain.fragment.Location;
import com.xiangshui.server.domain.fragment.RushHour;

import java.util.List;

public class Area {

    private Integer area_id;

    private String address;
    private String city;
    private String area_img;
    private String title;
    private String notification;
    private String contact;

    private Integer status;

    private List<RushHour> rushHours;
    private Integer minute_start;


    private Location location;

    private List<String> imgs;

    private List<CapsuleType> types;

    public Integer getArea_id() {
        return area_id;
    }

    public void setArea_id(Integer area_id) {
        this.area_id = area_id;
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
