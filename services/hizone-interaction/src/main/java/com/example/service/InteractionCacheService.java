package com.example.service;

import com.example.hizone.dto.UserInteraction;
import com.example.hizone.dto.UserPost;

public interface InteractionCacheService {

    void setCache(String key, Object value);

    Object getCache(String key);

    UserInteraction getUserInteraction(UserPost userPost);

    void loadPostUserInteraction(Long postId);

    void addLikePost(UserPost userPost);

    void addCollectPost(UserPost userPost);

    void cancelLikePost(UserPost userPost);

    void cancelCollectPost(UserPost userPost);
}
