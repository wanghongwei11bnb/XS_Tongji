package com.xiangshui.tj.websocket.message;

import java.util.List;

public class UsageRateMessage extends SendMessage {

    public static UsageRateMessage last;


    private List<Object[]> data;

    public List<Object[]> getData() {
        return data;
    }

    public void setData(List<Object[]> data) {
        this.data = data;
    }

    public UsageRateMessage() {
        last = this;
    }
}
