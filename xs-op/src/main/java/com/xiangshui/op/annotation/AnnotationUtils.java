package com.xiangshui.op.annotation;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class AnnotationUtils {
    public static Map<String, String> getFinalAuthMap() {
        Map<String, String> finalAuthMap = new HashMap<>();
        for (Field field : AuthRequired.class.getDeclaredFields()) {
            try {
                finalAuthMap.put(field.getName(), String.valueOf(field.get(null)));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return finalAuthMap;
    }
}
