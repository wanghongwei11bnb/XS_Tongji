package com.xiangshui.tj.websocket.message;

import java.util.TreeMap;

public class GeneralMessage extends SendMessage {
    public int countArea;
    public int countCapsule;
    public int countBooking;
    public TreeMap<Integer, Integer> countAreaForCity;
    public TreeMap<Integer, Integer> countCapsuleForCity;
    public TreeMap<Integer, Integer> countBookingForCity;
}
