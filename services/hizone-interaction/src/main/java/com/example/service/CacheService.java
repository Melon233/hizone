package com.example.service;

import com.example.hizone.inter.UserInteraction;
import com.example.hizone.inter.UserPost;

public interface CacheService {

    void setCache(String key, Object value);

    Object getCache(String key);

    UserInteraction getUserInteraction(UserPost userPost);

    void loadPostUserInteraction(int postId);

    void addLikePost(UserPost userPost);
}
