package com.xiangshui.web.threadLocal;

import com.xiangshui.web.bean.Session;

public class SessionLocal {
    private static final ThreadLocal<Session> threadLocal = new ThreadLocal<>();

    public static void set(Session session) {
        threadLocal.set(session);
    }

    public static Session get() {
        return threadLocal.get();
    }

    public static void remove() {
        threadLocal.remove();
    }
}
