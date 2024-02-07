package com.demo.client.impl;

import com.demo.client.RedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Arrays;
import java.util.List;

@Component
public class RedisClientImpl implements RedisClient {

    private final JedisPool jedisPool;

    @Autowired
    public RedisClientImpl(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    private Jedis getJedisResource() {
        return jedisPool.getResource();
    }

    /* STRING TYPE */
    public String get(String key) {
        Jedis jedis = null;
        try {
            jedis = getJedisResource();
            return jedis.get(key);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public List<String> mGet(String keys) {
        Jedis jedis = null;
        try {
            jedis = getJedisResource();

            List<String> response = jedis.mget(keys);
            System.out.println("response: " + response);
            return jedis.mget(keys);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public Long getExpiryTime(String key) {
        Jedis jedis = null;
        try {
            jedis = getJedisResource();
            return jedis.ttl(key);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public void setExpiryTime(String key, Integer expiryTimeInSec) {
        Jedis jedis = null;
        try {
            jedis = getJedisResource();
            jedis.expire(key, expiryTimeInSec);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public void delete(String key) {
        Jedis jedis = null;
        try {
            jedis = getJedisResource();
            jedis.del(key);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    // Set without expiry
    public String set(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = getJedisResource();
            return jedis.set(key, value);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public String setEx(String key, int seconds, String value) {
        Jedis jedis = null;
        try {
            jedis = getJedisResource();
            return jedis.setex(key, seconds, value);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /* SET */
    public long addToSets(String key, String member) {
        Jedis jedis = null;
        Long response;
        try {
            jedis = getJedisResource();
            response = jedis.sadd(key, member);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return response;
    }

    public void setList(String key, List<String> values) {
        Jedis jedis = null;
        try {
            jedis = getJedisResource();
            redis.clients.jedis.Transaction transaction = jedis.multi();
            transaction.del(key);
            String[] strArray = new String[values.size()];
            strArray = values.toArray(strArray);
            transaction.rpush(key, strArray);
            transaction.exec();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public List<String> getList(String key) {
        Jedis jedis = null;
        try {
            jedis = getJedisResource();
            return jedis.lrange(key, 0, -1);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

    }
}

