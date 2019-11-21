package com.xiangshui.util.web.result;


import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Map;

public class Result {

    public int code;
    public String msg;
    public Map<String, Object> data;

    public Result(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Result(CodeMsg codeMsg) {
        this.code = codeMsg.code;
        this.msg = codeMsg.msg;
    }

    public Result setCode(int code) {
        this.code = code;
        return this;
    }

    public Result setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    public Result putData(String key, Object value) {

        if (data == null) {
            data = new HashMap<>();
        }
        data.put(key, value);
        return this;
    }


}
