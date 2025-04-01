package com.example.service.impl;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.hizone.dao.comment.PostComment;
import com.example.hizone.front.interaction.CollectPost;
import com.example.hizone.front.interaction.LikePost;
import com.example.hizone.inter.UserInteraction;
import com.example.hizone.inter.UserPost;
import com.example.hizone.utility.RedisUtility;
import com.example.service.CacheService;
import com.example.service.InteractionService;
import com.github.benmanes.caffeine.cache.Cache;

import co.elastic.clients.elasticsearch._types.query_dsl.Like;
import co.elastic.clients.elasticsearch.security.User;

@Service
public class CacheServiceImpl implements CacheService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private Cache<String, Object> caffeineCache;

    @Autowired
    private Cache<String, Set<LikePost>> caffeineCacheLikePostSet;

    @Autowired
    private Cache<String, Set<CollectPost>> caffeineCacheCollectPostSet;

    @Autowired
    private InteractionService interactionService;

    /**
     * 交互服务缓存
     * 帖子交互元数据（点赞数，收藏数，评论数） 通过键-交互元数据对象实现
     * 帖子点赞记录（user_id, post_id） 通过set实现，键为post_id
     * 帖子收藏记录（user_id, post_id） 通过set实现，键为post_id
     */
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
    // @Override
    // public void likePost(LikePost likePost) {
    // return;
    // }

    @Override
    public UserInteraction getUserInteraction(UserPost userPost) {
        UserInteraction userInteraction = new UserInteraction();
        // Set<LikePost> likePostSet = caffeineCacheLikePostSet.getIfPresent("post_like");
        // Set<CollectPost> collectPostSet = caffeineCacheCollectPostSet.getIfPresent("post_collect");
        // if(likePostSet != null){
        // if(likePostSet.contains(new LikePost(userPost.getPostId(),userPost.getUserId()))){
        // userInteraction.setLiked(true);
        // }
        // }
        // if(collectPostSet != null){
        // if(collectPostSet.contains(new CollectPost(userPost.getPostId(),userPost.getUserId()))){
        // userInteraction.setCollected(true);
        // }
        // }
        if (!redisTemplate.hasKey("post_like" + userPost.getPostId()) || !redisTemplate.hasKey("post_collect" + userPost.getPostId())) {
            loadPostUserInteraction(userPost.getPostId());
        }
        // System.out.println(new LikePost(userPost.getPostId(), userPost.getUserId()));
        // System.out.println(redisTemplate.opsForSet().members("post_like" + userPost.getPostId()));
        userInteraction.setLiked(redisTemplate.opsForSet().isMember("post_like" + userPost.getPostId(), new LikePost(userPost.getPostId(), userPost.getUserId())));
        userInteraction.setCollected(redisTemplate.opsForSet().isMember("post_collect" + userPost.getPostId(), new CollectPost(userPost.getPostId(), userPost.getUserId())));
        return userInteraction;
    }

    @Override
    public void loadPostUserInteraction(int postId) {
        List<LikePost> likePostList = interactionService.getLikePostList(postId);
        List<CollectPost> collectPostList = interactionService.getCollectPostList(postId);
        // Set<LikePost> likePostSet = new HashSet<>(likePostList);
        // Set<CollectPost> collectPostSet = new HashSet<>(collectPostList);
        redisTemplate.opsForSet().add("post_like" + postId, likePostList);
        redisTemplate.opsForSet().add("post_collect" + postId, collectPostList);
        // caffeineCacheLikePostSet.put("post_like", likePostSet);
        // caffeineCacheCollectPostSet.put("post_collect", collectPostSet);
    }

    @Override
    public void addLikePost(UserPost userPost) {
        redisTemplate.opsForSet().add("post_like" + userPost.getPostId(), new LikePost(userPost.getPostId(), userPost.getUserId()));
    }
}
