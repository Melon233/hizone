package com.example.service;

public interface CacheService {

    Object getCache(String key);

    void setCache(String key, Object value);

    void deleteCache(String string);
}
