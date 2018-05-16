package com.xiangshui.server.exception;

import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;

public class XiangShuiException extends RuntimeException {
    public XiangShuiException() {
    }

    public XiangShuiException(String message) {
        super(message);
    }

    public XiangShuiException(CodeMsg codeMsg) {
        super(codeMsg.msg);
    }


    public Result toResult() {
        return new Result(-1, this.getMessage());
    }

}
