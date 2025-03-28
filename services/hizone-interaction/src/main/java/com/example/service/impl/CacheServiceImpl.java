package com.example.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.example.service.CacheService;
import com.github.benmanes.caffeine.cache.Cache;

public class CacheServiceImpl implements CacheService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private Cache<String, Object> caffeineCache;

    @Override
    public void setCache(String key, Object value) {
        caffeineCache.put(key, value);
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public Object getCache(String key) {
        Object cache;
        cache = caffeineCache.getIfPresent(key);
        if (cache != null) {
            return cache;
        }
        cache = redisTemplate.opsForValue().get(key);
        if (cache != null) {
            caffeineCache.put(key, cache);
            return cache;
        }
        return cache;
    }
}
