package com.xiangshui.util.web.converter;

import org.springframework.core.convert.converter.Converter;


public class BooleanConverter implements Converter<String, Boolean> {
    @Override
    public Boolean convert(String s) {
        return "1".equals(s) || "true".equals(s) || "True".equals(s);
    }
}
