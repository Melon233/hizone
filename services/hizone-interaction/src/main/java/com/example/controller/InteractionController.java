package com.example.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.hizone.dao.interaction.Interaction;
import com.example.hizone.front.interaction.CancelCollectPost;
import com.example.hizone.front.interaction.CancelLikePost;
import com.example.hizone.front.interaction.CollectPost;
import com.example.hizone.front.interaction.LikePost;
import com.example.hizone.inter.PostId;
import com.example.hizone.inter.UpdateCommentCount;
import com.example.hizone.inter.UserInteraction;
import com.example.hizone.inter.UserPost;
import com.example.hizone.outer.InteractionDetail;
import com.example.hizone.utility.Utility;
import com.example.service.CacheService;
import com.example.service.InteractionService;

import java.util.ArrayList;
import java.util.List;

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
    public InteractionDetail getInteractionDetail(@RequestHeader(value = "Token", required = false) String token, @RequestParam("post_id") int postId) {
        // System.out.println("getInteraction" + postId);
        UserInteraction userInteraction = new UserInteraction();
        if (token != null) {
            int userId = Utility.extractUserId(token);
            // 抓取单个用户帖子交互缓存
            userInteraction = cacheService.getUserInteraction(new UserPost(postId, userId));
            // System.out.println(userInteraction);
            // 未命中
            if (userInteraction == null) {
                // 从数据库抓取
                userInteraction = interactionService.getUserInteraction(new UserPost(postId, userId));
                System.out.println(userInteraction);
                // 缓存帖子所有交互
                cacheService.loadPostUserInteraction(postId);
            }
        } else {
            userInteraction.setLiked(false);
            userInteraction.setCollected(false);
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

    @GetMapping("/getInteractionDetailList")
    public List<InteractionDetail> getInteractionDetailList(@RequestHeader(value = "Token", required = false) String token, @RequestParam("post_id_list") int[] postIdList) {
        List<InteractionDetail> interactionDetailList = new ArrayList<>();
        int userId;
        if (token != null) {
            userId = Utility.extractUserId(token);
        } else {
            userId = -1;
        }
        for (int postId : postIdList) {
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
            UserInteraction userInteraction = new UserInteraction();
            if (token != null) {
                // 抓取单个用户帖子交互缓存
                userInteraction = cacheService.getUserInteraction(new UserPost(postId, userId));
                // System.out.println(userInteraction);
                // 未命中
                if (userInteraction == null) {
                    // 从数据库抓取
                    userInteraction = interactionService.getUserInteraction(new UserPost(postId, userId));
                    // 缓存帖子所有交互
                    cacheService.loadPostUserInteraction(postId);
                }
            } else {
                userInteraction.setLiked(false);
                userInteraction.setCollected(false);
            }
            InteractionDetail interactionDetail = new InteractionDetail();
            interactionDetail.setPostId(interaction.getPostId());
            interactionDetail.setLikeCount(interaction.getLikeCount());
            interactionDetail.setCollectCount(interaction.getCollectCount());
            interactionDetail.setCommentCount(interaction.getCommentCount());
            interactionDetail.setLiked(userInteraction.isLiked());
            interactionDetail.setCollected(userInteraction.isCollected());
            interactionDetailList.add(interactionDetail);
        }
        return interactionDetailList;
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
        // 异步更新数据库
        interactionService.updatePostLikeCount(likePost.getPostId(), 1);
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
        // 抓取用户单个帖子收藏记录缓存
        UserInteraction userInteraction = cacheService.getUserInteraction(new UserPost(collectPost.getPostId(), collectPost.getSenderId()));
        System.out.println(userInteraction);
        // if (userInteraction == null) {
        interactionService.addCollectPost(collectPost);
        cacheService.addCollectPost(new UserPost(collectPost.getPostId(), collectPost.getSenderId()));
        // 抓取单个帖子交互元数据缓存
        Interaction interaction = (Interaction) cacheService.getCache("interaction" + collectPost.getPostId());
        // 若非空
        if (interaction != null) {
            // 更新缓存
            interaction.setCollectCount((interaction.getCollectCount() + 1));
            cacheService.setCache("interaction" + collectPost.getPostId(), interaction);
        }
        // 异步更新数据库
        interactionService.updatePostCollectCount(collectPost.getPostId(), 1);
        return "success";
        // }
        // return "error";
    }

    @PostMapping("/cancelLikePost")
    public String cancelLikePost(@RequestBody CancelLikePost cancelLikePost) {
        // 抓取用户单个帖子点赞记录缓存
        UserInteraction userInteraction = cacheService.getUserInteraction(new UserPost(cancelLikePost.getPostId(), cancelLikePost.getSenderId()));
        System.out.println(userInteraction);
        // if (userInteraction == null) {
        interactionService.cancelLikePost(cancelLikePost);
        cacheService.cancelLikePost(new UserPost(cancelLikePost.getPostId(), cancelLikePost.getSenderId()));
        // 抓取单个帖子交互元数据缓存
        Interaction interaction = (Interaction) cacheService.getCache("interaction" + cancelLikePost.getPostId());
        // 若非空
        if (interaction != null) {
            // 更新缓存
            interaction.setLikeCount(interaction.getLikeCount() - 1);
            cacheService.setCache("interaction" + cancelLikePost.getPostId(), interaction);
        }
        // 异步更新数据库
        interactionService.updatePostLikeCount(cancelLikePost.getPostId(), 1);
        return "success";
        // }
        // return "error";
    }

    @PostMapping("/cancelCollectPost")
    public String cancelCollectPost(@RequestBody CancelCollectPost cancelCollectPost) {
        // 抓取用户单个帖子点赞记录缓存
        UserInteraction userInteraction = cacheService.getUserInteraction(new UserPost(cancelCollectPost.getPostId(), cancelCollectPost.getSenderId()));
        System.out.println(userInteraction);
        // if (userInteraction == null) {
        interactionService.cancelCollectPost(cancelCollectPost);
        cacheService.cancelCollectPost(new UserPost(cancelCollectPost.getPostId(), cancelCollectPost.getSenderId()));
        // 抓取单个帖子交互元数据缓存
        Interaction interaction = (Interaction) cacheService.getCache("interaction" + cancelCollectPost.getPostId());
        // 若非空
        if (interaction != null) {
            // 更新缓存
            interaction.setCollectCount(interaction.getCollectCount() - 1);
            cacheService.setCache("interaction" + cancelCollectPost.getPostId(), interaction);
        }
        // 异步更新数据库
        interactionService.updatePostCollectCount(cancelCollectPost.getPostId(), -1);
        return "success";
        // }
        // return "error";
    }

    @PostMapping("/updateCommentCount")
    public String updateCommentCount(@RequestBody UpdateCommentCount updateCommentCount) {
        Interaction interaction = (Interaction) cacheService.getCache("interaction" + updateCommentCount.getPostId());
        interaction.setCommentCount(interaction.getCommentCount() + updateCommentCount.getIncrement());
        cacheService.setCache("interaction" + updateCommentCount.getPostId(), interaction);
        interactionService.updateCommentCount(updateCommentCount);
        return "success";
    }

    @PostMapping("/initInteraction")
    public String initInteraction(@RequestBody PostId postId) {
        System.out.println("initInteraction" + postId.getPostId());
        interactionService.initInteraction(postId.getPostId());
        return "success";
    }
}
