package com.example.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.feign.UserFeignClient;
import com.example.fenta.dao.interaction.Interaction;
import com.example.fenta.front.interaction.CollectPost;
import com.example.fenta.front.interaction.ForwardPost;
import com.example.fenta.front.interaction.LikePost;
import com.example.fenta.inter.UpdateCommentCount;
import com.example.service.CacheService;
import com.example.service.InteractionService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@CrossOrigin
@RestController
public class InteractionController {

    @Autowired
    UserFeignClient userFeignClient;

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
    public Interaction getInteraction(@RequestParam("post_id") int postId) {
        Interaction interaction = (Interaction) cacheService.getCache("interaction" + postId);
        if (interaction != null) {
            return interaction;
        }
        interaction = interactionService.getInteraction(postId);
        cacheService.setCache("interaction" + postId, interaction);
        return interaction;
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
            return "success";
        }
        interaction = interactionService.getInteraction(likePost.getPostId());
        interaction.setLikeCount(interaction.getLikeCount() + 1);
        cacheService.setCache("interaction" + likePost.getPostId(), interaction);
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

    @PostMapping("/forwardPost")
    public String forwardPost(@RequestBody ForwardPost forwardPost) {
        interactionService.forwardPost(forwardPost);
        return "success";
    }
}
