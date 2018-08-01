package com.xiangshui.server.crud;

import java.lang.reflect.Field;

public abstract class CollectCallback {
    public abstract void run(String fieldName, Field field, String columnName, Object columnValue);
}