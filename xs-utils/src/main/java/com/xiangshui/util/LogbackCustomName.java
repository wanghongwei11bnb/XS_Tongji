package com.xiangshui.util;

import ch.qos.logback.core.PropertyDefinerBase;

public class LogbackCustomName extends PropertyDefinerBase {
    public String getPropertyValue() {
        return System.getProperties().getProperty("user.home");
    }

    public static void main(String[] args) {
        System.out.println(System.getProperties().getProperty("user.home"));
    }
}
