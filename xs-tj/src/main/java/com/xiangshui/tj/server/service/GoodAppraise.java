package com.xiangshui.tj.server.service;

import com.alibaba.fastjson.JSON;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

@Component
public class GoodAppraise {
    private String[] ts;

    public void init() {
        try {
            Set<String> set = new HashSet<>();
            for (String text : IOUtils.readLines(this.getClass().getResourceAsStream("/haoping.txt"), "UTF-8")) {
                if (StringUtils.isNotBlank(text)) {
                    set.add(text);
                }
            }
            ts = set.toArray(new String[set.size()]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String random() {
        return ts[(int) (Math.random() * ts.length)];
    }

}
