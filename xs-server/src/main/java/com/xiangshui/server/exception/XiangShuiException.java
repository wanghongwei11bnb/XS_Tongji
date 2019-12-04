package com.xiangshui.server.exception;

import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;

public class XiangShuiException extends RuntimeException {


    public int code = -1;
    public String msg;

    public XiangShuiException() {
    }

    public XiangShuiException(String msg) {
        super(msg);
        this.msg = msg;
    }


    public XiangShuiException(int code) {
        this.code = code;
    }

    public XiangShuiException(int code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public XiangShuiException(CodeMsg codeMsg) {
        super(codeMsg.msg);
        this.code = codeMsg.code;
        this.msg = codeMsg.msg;
    }


    public int getCode() {
        return code;
    }

    public XiangShuiException setCode(int code) {
        this.code = code;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public XiangShuiException setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public Result toResult() {
        return new Result(this.code, this.msg);
    }

}
