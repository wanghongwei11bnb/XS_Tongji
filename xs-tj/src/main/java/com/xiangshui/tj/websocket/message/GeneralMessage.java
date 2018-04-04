package com.xiangshui.tj.websocket.message;

import java.util.TreeMap;

public class GeneralMessage extends SendMessage {
    public int countArea;
    public int countCapsule;
    public int countBooking;
    public TreeMap<String, Integer> countAreaForCity;
    public TreeMap<String, Integer> countCapsuleForCity;
    public TreeMap<String, Integer> countBookingForCity;
}
