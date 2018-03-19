package com.xiangshui.tj.websocket.message;

import java.util.List;

public class ListMessage extends SendMessage {
    private List<SendMessage> messageList;

    public List<SendMessage> getMessageList() {
        return messageList;
    }

    public void setMessageList(List<SendMessage> messageList) {
        this.messageList = messageList;
    }
}
