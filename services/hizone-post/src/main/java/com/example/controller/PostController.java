package com.example.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.feign.InteractionFeignClient;
import com.example.feign.UserFeignClient;
import com.example.fenta.dao.interaction.Interaction;
import com.example.fenta.dao.post.Post;
import com.example.fenta.front.post.DeletePost;
import com.example.fenta.front.post.ModifyPost;
import com.example.fenta.front.post.UploadPost;
import com.example.fenta.inter.PostId;
import com.example.fenta.inter.UpdateUserMetadata;
import com.example.fenta.outer.PostDetail;
import com.example.service.CacheService;
import com.example.service.PostService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@CrossOrigin
@RestController
class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private UserFeignClient userFeignClient;

    @Autowired
    private InteractionFeignClient interactionFeignClient;

    /**
     * 获取单个帖子
     * 高频高精数据-缓存
     * 
     * @param postId
     * @return
     */
    @GetMapping("/getPost")
    public Post getPost(@RequestParam("post_id") int postId) {
        Post post = (Post) cacheService.getCache("post" + postId);
        if (post != null) {
            return post;
        }
        post = postService.getPost(postId);
        cacheService.setCache("post" + postId, post);
        return post;
    }

    /**
     * 获取我发布的帖子
     * 低频高精数据-不缓存
     * 
     * @param authorId
     * @return
     */
    @GetMapping("/getPostList")
    public List<Post> getPostList(@RequestParam int authorId) {
        return postService.getPostList(authorId);
    }

    /**
     * 获取推送
     * 算法
     * 
     * @param authorId
     * @return
     */
    @GetMapping("/getPush")
    public List<PostDetail> getPush() {
        List<Post> pushList = postService.getPush();
        List<PostDetail> postDetailList = new ArrayList<>();
        for (Post post : pushList) {
            Interaction interaction = interactionFeignClient.getInteraction(post.getPostId());
            PostDetail postDetail = new PostDetail();
            postDetail.setPostId(post.getPostId());
            postDetail.setAuthorId(post.getAuthorId());
            postDetail.setAuthorName(post.getAuthorName());
            postDetail.setPostTitle(post.getPostTitle());
            postDetail.setPostContent(post.getPostContent());
            postDetail.setLikeCount(interaction.getLikeCount());
            postDetail.setCollectCount(interaction.getCollectCount());
            postDetail.setCommentCount(interaction.getCommentCount());
            postDetail.setPostTime(post.getPostTime());
            postDetailList.add(postDetail);
        }
        return postDetailList;
    }

    /**
     * 上传帖子
     * 高频高精数据-插入数据库
     * 
     * @param uploadPost
     * @return
     */
    @PostMapping("/uploadPost")
    public String uploadPost(@RequestBody UploadPost uploadPost) {
        postService.insertPost(uploadPost);
        UpdateUserMetadata updateUserMetadata = new UpdateUserMetadata();
        updateUserMetadata.setUserId(uploadPost.getAuthorId());
        updateUserMetadata.setPostCount(1);
        userFeignClient.updateUserMetadata(updateUserMetadata);
        PostId postId = new PostId();
        postId.setPostId(uploadPost.getPostId());
        System.out.println(interactionFeignClient.initInteraction(postId));
        System.out.println(uploadPost.getPostId());
        return "success";
    }

    /**
     * 修改帖子
     * 高频高精数据-先改数据库再删缓存
     * 
     * @param entity
     * @return
     */
    @PostMapping("/modifyPost")
    public String modifyPost(@RequestBody ModifyPost modifyPost) {
        postService.modifyPost(modifyPost);
        cacheService.deleteCache("post" + modifyPost.getPostId());
        return "success";
    }

    /**
     * 删除帖子
     * 高精高频数据-先删数据库再删缓存
     * 
     * @param deletePost
     * @return
     */
    @PostMapping("/deletePost")
    public String deletePost(@RequestBody DeletePost deletePost) {
        postService.deletePost(deletePost.getPostId());
        cacheService.deleteCache("post" + deletePost.getPostId());
        UpdateUserMetadata updateUserMetadata = new UpdateUserMetadata();
        updateUserMetadata.setUserId(deletePost.getAuthorId());
        updateUserMetadata.setPostCount(-1);
        userFeignClient.updateUserMetadata(updateUserMetadata);
        return "success";
    }
}