package com.xiangshui.util;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class ClassUtils {
    private static final Logger log = LoggerFactory.getLogger(ClassUtils.class);


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


    public static Class<?> getPattern(Class<?> clazz, int index) {
        if (clazz == null) {
            return null;
        }
        ParameterizedType parameterizedType = (ParameterizedType) clazz.getGenericSuperclass();
        Type[] types = parameterizedType.getActualTypeArguments();
        if (types == null || types.length > index) {
            if (types[index] instanceof Class) {
                return (Class<?>) types[index];
            }
        }
        return null;
    }

}
