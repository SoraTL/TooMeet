package com.toomeet.user.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Configuration
public class RedisConfig {
    @Value("${spring.redis.uri}")
    private String redisUri;

    @Bean
    public Jedis jedis() {
        try (JedisPool pool = new JedisPool(redisUri)) {
            return pool.getResource();
        }
    }

}
