package com.example.service;

public interface CacheService {

    void setCache(String key, Object value);

    Object getCache(String key);
}
