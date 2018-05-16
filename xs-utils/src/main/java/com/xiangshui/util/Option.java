package com.xiangshui.util;

import com.alibaba.fastjson.JSON;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class Option<T> {
    public final T value;
    public final String text;

    public Option(T value, String text) {
        this.value = value;
        this.text = text;
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

}
