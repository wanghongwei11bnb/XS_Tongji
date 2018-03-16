package com.xiangshui.tj.websocket;

public class SendMessage {
    private final int messageType;

    public SendMessage(int messageType) {
        this.messageType = messageType;
    }

    public static final int MESSAGE_TYPE_DATA = 1;

}
