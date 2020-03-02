package com.xiangshui.util.web.converter;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.springframework.core.convert.converter.Converter;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LocalDateConverter implements Converter<String, LocalDate> {


    @Override
    public LocalDate convert(String s) {
        if (StringUtils.isBlank(s)) {
            return null;
        }
        Date date = null;
        try {
            if (s.matches("\\d+")) {
                date = new Date(Long.valueOf(s));
            } else if (s.matches("\\d{4}-\\d{1,2}-\\d{1,2}")) {
                date = new SimpleDateFormat("yyyy-MM-dd").parse(s);
            } else if (s.matches("\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}")) {
                date = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(s);
            } else if (s.matches("\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}")) {
                date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(s);
            } else {
                return null;
            }
            return new LocalDate(date);
        } catch (Exception e) {
            return null;
        }
    }
}