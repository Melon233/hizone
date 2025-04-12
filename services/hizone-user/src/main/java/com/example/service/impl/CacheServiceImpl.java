package com.example.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.service.UserCacheService;
import com.github.benmanes.caffeine.cache.Cache;

@Service
public class CacheServiceImpl implements UserCacheService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private Cache<String, Object> caffeineCache;

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

    @Override
    public void setCache(String key, Object value) {
        caffeineCache.put(key, value);
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public void deleteCache(String key) {
        redisTemplate.delete(key);
        caffeineCache.invalidate(key);
    }
}
