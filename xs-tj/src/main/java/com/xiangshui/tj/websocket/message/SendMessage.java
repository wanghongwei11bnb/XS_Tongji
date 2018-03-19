package com.xiangshui.tj.websocket.message;

abstract public class SendMessage {
    private final String messageType = this.getClass().getSimpleName();

    public String getMessageType() {
        return messageType;
    }



}
