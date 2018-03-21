package com.xiangshui.tj.websocket.message;

import com.xiangshui.tj.server.bean.Appraise;
import com.xiangshui.tj.server.bean.City;

import java.util.List;

public class InitAppraiseMessage extends SendMessage {
    private List<Appraise> appraiseList;

    public List<Appraise> getAppraiseList() {
        return appraiseList;
    }

    public void setAppraiseList(List<Appraise> appraiseList) {
        this.appraiseList = appraiseList;
    }
}
