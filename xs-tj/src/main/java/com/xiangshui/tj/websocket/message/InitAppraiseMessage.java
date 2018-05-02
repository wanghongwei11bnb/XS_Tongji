package com.xiangshui.tj.websocket.message;

import com.xiangshui.tj.server.bean.AppraiseTj;

import java.util.List;

public class InitAppraiseMessage extends SendMessage {
    private List<AppraiseTj> appraiseList;

    public List<AppraiseTj> getAppraiseList() {
        return appraiseList;
    }

    public void setAppraiseList(List<AppraiseTj> appraiseList) {
        this.appraiseList = appraiseList;
    }
}
