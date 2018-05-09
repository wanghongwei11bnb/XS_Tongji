package com.xiangshui.server.constant;

public class AreaStatus {
    public final int id;
    public final String name;

    public AreaStatus(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static final AreaStatus ON_LINE = new AreaStatus(0, "正常");
    public static final AreaStatus DOWN_LINE = new AreaStatus(-1, "已下线");
    public static final AreaStatus AWAIT = new AreaStatus(-2, "待运营");
}
