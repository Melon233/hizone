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
     * 
     * @param postLike
     * @return
     */
    @GetMapping("/getInteraction")
    public InteractionDetail getInteraction(@RequestHeader("Cookie") String token, @RequestParam("post_id") int postId) {
        System.out.println("getInteraction" + postId);
        int userId = Utility.extractUserId(token);
        InteractionDetail interactionDetail = new InteractionDetail();
        UserPost userPost = new UserPost();
        userPost.setPostId(postId);
        userPost.setUserId(userId);
        UserInteraction userInteraction = interactionService.getUserInteraction(userPost);
        Interaction interaction = (Interaction) cacheService.getCache("interaction" + postId);
        if (interaction != null) {
            System.out.println("get from cache" + interaction);
            interactionDetail.setPostId(interaction.getPostId());
            return interactionDetail;
        }
        interaction = interactionService.getInteraction(postId);
        cacheService.setCache("interaction" + postId, interaction);
        System.out.println("get from db" + interaction);
        interactionDetail.setPostId(interaction.getPostId());
        interactionDetail.setLikeCount(interaction.getLikeCount());
        interactionDetail.setCollectCount(interaction.getCollectCount());
        interactionDetail.setCommentCount(interaction.getCommentCount());
        interactionService.getUserInteraction(userPost);
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
        interactionService.addLikePost(likePost);
        Interaction interaction = (Interaction) cacheService.getCache("interaction" + likePost.getPostId());
        if (interaction != null) {
            interaction.setLikeCount(interaction.getLikeCount() + 1);
            cacheService.setCache("interaction" + likePost.getPostId(), interaction);
            System.out.println("update cache" + interaction);
            return "success";
        }
        interaction = interactionService.getInteraction(likePost.getPostId());
        interaction.setLikeCount(interaction.getLikeCount() + 1);
        cacheService.setCache("interaction" + likePost.getPostId(), interaction);
        System.out.println("update cache by db" + interaction);
        return "success";
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
