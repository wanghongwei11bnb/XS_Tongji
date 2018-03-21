package com.xiangshui.tj.websocket.message;

import java.util.List;
import java.util.Map;

public class CumulativeBookingMessage extends SendMessage {

    private List<Object[]> data;

    public List<Object[]> getData() {
        return data;
    }

    public void setData(List<Object[]> data) {
        this.data = data;
    }
}
