package com.xiangshui.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class Option<T> {
    public final T value;
    public final String text;
    public final String color;


    public Option(T value, String text) {
        this.value = value;
        this.text = text;
        this.color = null;
    }

    public Option(T value, String text, String color) {
        this.value = value;
        this.text = text;
        this.color = color;
    }


    public static String getActiveText(List<? extends Option> optionList, Object value) {
        for (Option option : optionList) {
            if (option.value.equals(value)) {
                return option.text;
            }
        }
        return null;
    }


    public static List<Option> getOptions(Class<? extends Option> optionClass) {
        List<Option> optionList = new ArrayList<Option>();
        if (optionClass != null) {
            Field[] fields = optionClass.getDeclaredFields();
            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers()) && Option.class.isAssignableFrom(field.getType())) {
                    try {
                        optionList.add((Option) field.get(optionClass));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return optionList;
    }

    public static class Color {
        public static final String color_primary = "primary";
        public static final String color_secondary = "secondary";
        public static final String color_success = "success";
        public static final String color_info = "info";
        public static final String color_warning = "warning";
        public static final String color_danger = "danger";
    }

}
