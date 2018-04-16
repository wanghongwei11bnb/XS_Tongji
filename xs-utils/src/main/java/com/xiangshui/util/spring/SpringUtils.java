package com.xiangshui.util.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringUtils {


    private static ApplicationContext applicationContext;

    private static boolean inited;

    public static void init() {
        init("spring/applicationContext.xml");
    }

    public static void init(String configLocation) {
        if (!inited) {
            applicationContext = new ClassPathXmlApplicationContext(configLocation);
            inited = true;
        }
    }

    public static <T> T getBean(Class<T> requiredType) {
        return applicationContext.getBean(requiredType);
    }

    public static Object getBean(String id) {
        return applicationContext.getBean(id);
    }

    public static void main(String[] args) {

    }
}
