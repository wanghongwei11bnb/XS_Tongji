package com.xiangshui.server.dao.redis;

import com.alibaba.fastjson.JSON;
import com.xiangshui.util.CallBack;
import com.xiangshui.util.CallBackForResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Date;

@Service
public class RedisService implements InitializingBean {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${redis.host}")
    private String host;
    @Value("${redis.port}")
    private int port;
    @Value("${redis.password}")
    private String password;


    @Value("${isdebug}")
    private boolean debug;

    public JedisPool jedisPool;

    public String ambientPrefix() {
        return debug ? "dev_" : "online_";
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


    public long del(KeyPrefix keyPrefix, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = ambientPrefix() + keyPrefix.getRealKey(key);
            return jedis.del(realKey);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public long del(KeyPrefix keyPrefix) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = ambientPrefix() + keyPrefix.getRealKey();
            return jedis.del(realKey);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }


    public boolean exists(KeyPrefix keyPrefix, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = ambientPrefix() + keyPrefix.getRealKey(key);
            return jedis.exists(realKey);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public boolean exists(KeyPrefix keyPrefix) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = ambientPrefix() + keyPrefix.getRealKey();
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
            String realKey = ambientPrefix() + keyPrefix.getRealKey(key);
            return jedis.expire(realKey, keyPrefix.expiry);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public long expire(KeyPrefix keyPrefix) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = ambientPrefix() + keyPrefix.getRealKey();
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
            String realKey = ambientPrefix() + keyPrefix.getRealKey(key);
            String str = beanToString(value);
            if (keyPrefix.expiry > 0) {
                jedis.setex(realKey, keyPrefix.expiry, str);
            } else {
                jedis.set(realKey, str);
            }
            return true;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public <T> boolean set(KeyPrefix keyPrefix, T value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = ambientPrefix() + keyPrefix.getRealKey();
            String str = beanToString(value);
            if (keyPrefix.expiry > 0) {
                jedis.setex(realKey, keyPrefix.expiry, str);
            } else {
                jedis.set(realKey, str);
            }
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
            String realKey = ambientPrefix() + keyPrefix.getRealKey(key);
            String str = jedis.get(realKey);
            T obj = stringToBean(str, clazz);
            return obj;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public <T> T get(KeyPrefix keyPrefix, Class<T> clazz) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = ambientPrefix() + keyPrefix.getRealKey();
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
            String realKey = ambientPrefix() + keyPrefix.getRealKey(key);
            return jedis.incr(realKey);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public Long incr(final KeyPrefix keyPrefix) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = ambientPrefix() + keyPrefix.getRealKey();
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

    public <R> R run(CallBackForResult<Jedis, R> callBack) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return callBack.run(jedis);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }


    public void afterPropertiesSet() throws Exception {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        jedisPool = new JedisPool(config, host, port, 1000 * 30);
        this.del(CityKeyPrefix.list_active);
        this.del(CityKeyPrefix.list_all);
    }

    public static void main(String[] args) {
        Jedis jedis = new Jedis("dev.xiangshuispace.com", 6379);
//        jedis.auth("xiangshui@123");
        jedis.setex("test", 60 * 3, "123123");
    }
}
