package com.xiangshui.util;

import ch.qos.logback.core.PropertyDefinerBase;

public class LogbackCustomName extends PropertyDefinerBase {
    public String getPropertyValue() {
        return System.getProperty("user.home");
    }

    public static void main(String[] args) {
    }
}
