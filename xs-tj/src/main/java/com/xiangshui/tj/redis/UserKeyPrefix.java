package com.xiangshui.tj.redis;

public class UserKeyPrefix extends KeyPrefix {

    public UserKeyPrefix(String prefix) {
        super(prefix);
    }

    public UserKeyPrefix(String prefix, int expiry) {
        super(prefix, expiry);
    }

    public static final KeyPrefix user_token = new UserKeyPrefix("user_token", 1000 * 60 * 60 * 24 * 7);
    public static final KeyPrefix tech_article = new UserKeyPrefix("tech_article", 1000 * 60 * 10);


    public static void main(String[] args) {
        System.out.println(user_token.getRealKey("dsfs"));
    }

}
