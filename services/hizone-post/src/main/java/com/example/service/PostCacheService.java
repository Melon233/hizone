package com.example.service;

public interface PostCacheService {

    void setCache(String key, Object value);

    Object getCache(String key);

    void deleteCache(String string);
}
