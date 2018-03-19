package com.xiangshui.util;

import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class ClassUtils {

    public static Map<String, Object> getStatic(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }
        Field[] fields = clazz.getDeclaredFields();
        Map<String, Object> map = new HashMap<String, Object>();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) {
                try {
                    map.put(field.getName(), field.get(clazz));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return map;
    }

    public static JSONObject getStaticJSON(Class<?> clazz) {
        Map<String, Object> map = getStatic(clazz);
        if (map == null) {
            return null;
        }
        return new JSONObject(map);
    }


    public static void getPattern(Class<?> clazz, int index) {
        while (clazz != Object.class) {
            Type type = clazz.getGenericSuperclass();
            if (type instanceof ParameterizedType) {
                Type[] args = ((ParameterizedType) type).getActualTypeArguments();
                if (args[0] instanceof Class) {

                }
            }
        }
    }

    public static void main(String[] args) {

    }

}
