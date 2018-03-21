package com.xiangshui.tj.web.result;

public class CodeMsg {

    public final int code;
    public final String msg;

    private CodeMsg(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static final CodeMsg SUCCESS = new CodeMsg(0, "success");
    public static final CodeMsg SERVER_ERROR = new CodeMsg(500, "未知错误");
    public static final CodeMsg CONNEC_TIMEOUT = new CodeMsg(500, "连接超时");
    public static final CodeMsg ILLEGAL_REQUEST = new CodeMsg(400, "连接超时");

}
