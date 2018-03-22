package com.xiangshui.tj.server.redis;

import com.alibaba.fastjson.JSON;
import com.xiangshui.util.CallBack;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Date;

@Service
public class RedisService {
    private static final Logger log = LoggerFactory.getLogger(RedisService.class);

    @Value("${redis.host.dev}")
    private String host_dev;
    @Value("${redis.host.www}")
    private String host_www;
    @Value("${redis.port.dev}")
    private int port_dev;
    @Value("${redis.port.www}")
    private int port_www;
    @Value("${redis.password}")
    private String password;


    @Value("${isdebug}")
    private boolean debug;

    public JedisPool jedisPool;

    private boolean inited;

    public void init() {
        if (!inited) {
            GenericObjectPoolConfig config = new GenericObjectPoolConfig();
            jedisPool = new JedisPool(config, debug ? host_dev : host_www, debug ? port_dev : port_www, 1000 * 30, password);
            inited = true;
        }
    }


    public String getRealKey(KeyPrefix keyPrefix, String key) {
        return keyPrefix.prefix + ":" + key;
    }

    private <T> String beanToString(T value) {
        if (value == null) {
            return null;
        }
        Class<?> clazz = value.getClass();
        if (
                clazz == boolean.class || clazz == Boolean.class
                        || clazz == char.class || clazz == Character.class
                        || clazz == byte.class || clazz == Byte.class
                        || clazz == short.class || clazz == Short.class
                        || clazz == int.class || clazz == Integer.class
                        || clazz == long.class || clazz == Long.class
                        || clazz == double.class || clazz == Double.class
                        || clazz == float.class || clazz == Float.class
                ) {
            return "" + value;
        } else if (clazz == String.class) {
            return (String) value;
        } else if (clazz == Date.class) {
            return "" + ((Date) value).getTime();
        } else {
            return JSON.toJSONString(value);
        }
    }

    private <T> T stringToBean(String str, Class<T> clazz) {
        if (StringUtils.isBlank(str)) {
            return null;
        }
        if (clazz == byte.class || clazz == Byte.class) {
            return (T) Byte.valueOf(str);
        } else if (clazz == short.class || clazz == Short.class) {
            return (T) Short.valueOf(str);
        } else if (clazz == String.class) {
            return (T) str;
        } else if (clazz == Date.class) {
            return (T) new Date(Long.valueOf(str));
        } else {
            return JSON.parseObject(str, clazz);
        }
    }


    public boolean exists(KeyPrefix keyPrefix, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = getRealKey(keyPrefix, key);
            return jedis.exists(realKey);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public long expire(KeyPrefix keyPrefix, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = getRealKey(keyPrefix, key);
            return jedis.expire(realKey, keyPrefix.expiry);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public <T> boolean set(KeyPrefix keyPrefix, String key, T value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = getRealKey(keyPrefix, key);
            String str = beanToString(value);
            if (keyPrefix.expiry > 0) {
                jedis.setex(realKey, keyPrefix.expiry, str);
            } else {
                jedis.set(realKey, str);
            }
            jedis.set(realKey, str);

            return true;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public <T> T get(KeyPrefix keyPrefix, String key, Class<T> clazz) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = getRealKey(keyPrefix, key);
            String str = jedis.get(realKey);
            T obj = stringToBean(str, clazz);
            return obj;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }


    public Long incr(KeyPrefix keyPrefix, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = getRealKey(keyPrefix, key);
            return jedis.incr(realKey);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }


    public void run(CallBack<Jedis> callBack) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            callBack.run(jedis);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }


    public static void main(String[] args) {

        RedisService redisService = new RedisService();

        redisService.set(TestKeyPrefix.test, "dfdf", "sdfssdgfdg");


//        TechArticle article = new TechArticle();
//        article.setId(123);
//        article.setTitle("123123124");
//        article.setSubtitle("123123124");
//        redisService.set(KeyPrefix.user_token, "B3D641ABD1C6425BB547DF90CE9AB0C2", article);


//        TechArticle article = redisService.get(KeyPrefix.user_token, "B3D641ABD1C6425BB547DF90CE9AB0C2", TechArticle.class);

//        System.out.println(JSON.toJSONString(article));
    }


}
