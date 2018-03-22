package com.xiangshui.tj.websocket.message;

import java.util.List;

public class CumulativeTimeMessage extends SendMessage {


    private List<Object[]> data;

    public List<Object[]> getData() {
        return data;
    }

    public void setData(List<Object[]> data) {
        this.data = data;
    }

}
