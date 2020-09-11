package com.xiangshui.util.web.converter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;

public class StringConverter implements Converter<String, String> {

    @Override
    public String convert(String s) {
        return StringUtils.isNotBlank(s) ? s.trim() : null;
    }
}