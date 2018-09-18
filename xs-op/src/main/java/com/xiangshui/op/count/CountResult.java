package com.xiangshui.op.count;

import com.alibaba.fastjson.JSONObject;

public class CountResult {
    private String type;
    private JSONObject data;
    private JSONObject options;

    public String getType() {
        return type;
    }

    public CountResult setType(String type) {
        this.type = type;
        return this;
    }

    public JSONObject getData() {
        return data;
    }

    public CountResult setData(JSONObject data) {
        this.data = data;
        return this;
    }

    public JSONObject getOptions() {
        return options;
    }

    public CountResult setOptions(JSONObject options) {
        this.options = options;
        return this;
    }
}
