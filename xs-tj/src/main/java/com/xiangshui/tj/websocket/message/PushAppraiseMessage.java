package com.xiangshui.tj.websocket.message;

import com.xiangshui.tj.server.bean.AppraiseTj;

public class PushAppraiseMessage extends SendMessage {
    private AppraiseTj appraise;

    public AppraiseTj getAppraise() {
        return appraise;
    }

    public void setAppraise(AppraiseTj appraise) {
        this.appraise = appraise;
    }
}
