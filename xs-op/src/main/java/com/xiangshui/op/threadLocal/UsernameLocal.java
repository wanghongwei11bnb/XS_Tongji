package com.xiangshui.op.threadLocal;

public class UsernameLocal {
    private static final ThreadLocal<String> threadLocal = new ThreadLocal<>();

    public static void set(String username) {
        threadLocal.set(username);
    }

    public static String get() {
        return threadLocal.get();
    }

    public static void remove() {
        threadLocal.remove();
    }
}
