package com.xiangshui.util.web.converter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateConverter implements Converter<String, Date> {

    public Date convert(String s) {
        if (StringUtils.isBlank(s)) {
            return null;
        }
        try {
            if (s.matches("\\d+")) {
                return new Date(Long.valueOf(s));
            } else if (s.matches("\\d{4}-\\d{1,2}-\\d{1,2}")) {
                return new SimpleDateFormat("yyyy-MM-dd").parse(s);
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }
}