package com.xiangshui.op.annotation;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Target;

@Documented
@Inherited
@Target(ElementType.METHOD)
public @interface AuthPassport {
    String value();
    public static final String VIEW_AREA_MANAGE = "VIEW_AREA_MANAGE";
}
