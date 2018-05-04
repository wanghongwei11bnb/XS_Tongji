package com.xiangshui.util.web.result;

public class CodeMsg {

    public final int code;
    public final String msg;

    private CodeMsg(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static final CodeMsg SUCCESS = new CodeMsg(0, "success");

    public static final CodeMsg BAD_REQUEST = new CodeMsg(400, "错误请求");
    public static final CodeMsg NO_LOGIN = new CodeMsg(401, "未登录");
    public static final CodeMsg NO_FOUND = new CodeMsg(404, "资源未找到");
    public static final CodeMsg AUTH_FAIL = new CodeMsg(405, "用户明或密码失败");

    public static final CodeMsg SERVER_ERROR = new CodeMsg(500, "未知错误");
    public static final CodeMsg TIMEOUT = new CodeMsg(501, "连接超时");


}
