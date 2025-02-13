package com.ihsan.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
public class CacheService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private final ObjectMapper objectMapper;

    public CacheService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule()); // ✅ JavaTimeModule ekledik!
    }

    private static final long CACHE_EXPIRATION = 30; // 30 dakika

    private String generateKey(String type, Object id) {
        return type + ":" + id;
    }

    public void put(String type, Object id, Object value) {
        String key = generateKey(type, id);
        redisTemplate.opsForValue().set(key, value, CACHE_EXPIRATION, TimeUnit.MINUTES);
    }

    public <T> T get(String type, Object id, Class<T> clazz) {
        String key = generateKey(type, id);
        Object cachedValue = redisTemplate.opsForValue().get(key);

        if (cachedValue == null) {
            return null;
        }

        return objectMapper.convertValue(cachedValue, clazz);
    }

    public void delete(String type, Object id) {
        String key = generateKey(type, id);
        redisTemplate.delete(key);
    }
}
