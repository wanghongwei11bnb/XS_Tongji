package com.xiangshui.tj.websocket.message;

import com.xiangshui.tj.server.bean.Appraise;

public class PushAppraiseMessage extends SendMessage {
    private Appraise appraise;

    public Appraise getAppraise() {
        return appraise;
    }

    public void setAppraise(Appraise appraise) {
        this.appraise = appraise;
    }
}
