package com.example.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.feign.UserFeignClient;
import com.example.hizone.dao.interaction.Interaction;
import com.example.hizone.front.interaction.CollectPost;
import com.example.hizone.front.interaction.ForwardPost;
import com.example.hizone.front.interaction.LikePost;
import com.example.hizone.inter.PostId;
import com.example.hizone.inter.UpdateCommentCount;
import com.example.hizone.inter.UserInteraction;
import com.example.hizone.inter.UserPost;
import com.example.hizone.outer.InteractionDetail;
import com.example.hizone.utility.Utility;
import com.example.service.CacheService;
import com.example.service.InteractionService;

import co.elastic.clients.elasticsearch.security.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@EnableFeignClients
@CrossOrigin
@RestController
public class InteractionController {

    @Autowired
    private UserFeignClient userFeignClient;

    @Autowired
    private InteractionService interactionService;

    @Autowired
    private CacheService cacheService;

    /**
     * 获取互动元数据
     * 高频低精数据-缓存
     * 从token解析uid，从缓存获取元数据，若缓存中无元数据，则从数据库获取元数据并缓存
     * 然后通过用户服务
     * 
     * @param postLike
     * @return
     */
    @GetMapping("/getInteractionDetail")
    public InteractionDetail getInteractionDetail(@RequestHeader(value = "Cookie") String token, @RequestParam("post_id") int postId) {
        // System.out.println("getInteraction" + postId);
        int userId = Utility.extractUserId(token);
        // 抓取单个用户帖子交互缓存
        UserInteraction userInteraction = cacheService.getUserInteraction(new UserPost(postId, userId));
        // System.out.println(userInteraction);
        // 未命中
        if (userInteraction == null) {
            // 从数据库抓取
            userInteraction = interactionService.getUserInteraction(new UserPost(postId, userId));
            System.out.println(userInteraction);
            // 缓存帖子所有交互
            cacheService.loadPostUserInteraction(postId);
        }
        // 抓取单个帖子交互元数据缓存
        Interaction interaction = (Interaction) cacheService.getCache("interaction" + postId);
        // 未命中
        if (interaction == null) {
            // 从数据库抓取
            interaction = interactionService.getInteraction(postId);
            // System.out.println("getInteraction by db" + interaction);
            // 加入缓存
            cacheService.setCache("interaction" + postId, interaction);
        }
        InteractionDetail interactionDetail = new InteractionDetail();
        interactionDetail.setPostId(interaction.getPostId());
        interactionDetail.setLikeCount(interaction.getLikeCount());
        interactionDetail.setCollectCount(interaction.getCollectCount());
        interactionDetail.setCommentCount(interaction.getCommentCount());
        interactionDetail.setLiked(userInteraction.isLiked());
        interactionDetail.setCollected(userInteraction.isCollected());
        return interactionDetail;
    }

    /**
     * 点赞帖子
     * 高频低精数据-记录异步到数据库修改缓存元数据
     * 
     * @param likePost
     * @return
     */
    @PostMapping("/likePost")
    public String likePost(@RequestBody LikePost likePost) {
        // 抓取用户单个帖子点赞记录缓存
        UserInteraction userInteraction = cacheService.getUserInteraction(new UserPost(likePost.getPostId(), likePost.getSenderId()));
        System.out.println(userInteraction);
        // if (userInteraction == null) {
        interactionService.addLikePost(likePost);
        cacheService.addLikePost(new UserPost(likePost.getPostId(), likePost.getSenderId()));
        // 抓取单个帖子交互元数据缓存
        Interaction interaction = (Interaction) cacheService.getCache("interaction" + likePost.getPostId());
        // 若非空
        if (interaction != null) {
            // 更新缓存
            interaction.setLikeCount(interaction.getLikeCount() + 1);
            cacheService.setCache("interaction" + likePost.getPostId(), interaction);
        }
        return "success";
        // }
        // return "error";
    }

    /**
     * 收藏帖子
     * 高频低精数据-记录异步到数据库修改缓存元数据
     * 
     * @param collectPost
     * @return
     */
    @PostMapping("/collectPost")
    public String collectPost(@RequestBody CollectPost collectPost) {
        interactionService.addCollectPost(collectPost);
        Interaction interaction = (Interaction) cacheService.getCache("interaction" + collectPost.getPostId());
        if (interaction != null) {
            interaction.setCollectCount(interaction.getCollectCount() + 1);
            cacheService.setCache("interaction" + collectPost.getPostId(), interaction);
            return "success";
        }
        interaction = interactionService.getInteraction(collectPost.getPostId());
        interaction.setCollectCount(interaction.getCollectCount() + 1);
        cacheService.setCache("interaction" + collectPost.getPostId(), interaction);
        return "success";
    }

    @PostMapping("/updateCommentCount")
    public String updateCommentCount(UpdateCommentCount updateCommentCount) {
        Interaction interaction = (Interaction) cacheService.getCache("interaction" + updateCommentCount.getPostId());
        interaction.setCommentCount(interaction.getCommentCount() + updateCommentCount.getCommentCount());
        cacheService.setCache("interaction" + updateCommentCount.getPostId(), interaction);
        return "success";
    }

    @PostMapping("/initInteraction")
    public String initInteraction(@RequestBody PostId postId) {
        System.out.println("initInteraction" + postId.getPostId());
        interactionService.initInteraction(postId.getPostId());
        return "success";
    }

    @PostMapping("/forwardPost")
    public String forwardPost(@RequestBody ForwardPost forwardPost) {
        interactionService.forwardPost(forwardPost);
        return "success";
    }
}
