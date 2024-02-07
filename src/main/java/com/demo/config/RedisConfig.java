package com.demo.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Bean;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Component
public class RedisConfig {
    private final ApplicationConfig applicationConfig;

    @Autowired
    public RedisConfig(ApplicationConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
    }

    public JedisPoolConfig prepareJedisPoolConfig() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(100);
        jedisPoolConfig.setMaxTotal(100);
        jedisPoolConfig.setMinIdle(100);
        return jedisPoolConfig;
    }
    @Bean
    public JedisPool createJedisPool() {
        return new JedisPool(prepareJedisPoolConfig(), applicationConfig.getRedisUrl(),
                applicationConfig.getRedisPort(), 5000, "password", 0);
    }
}
