package com.example.controller;

import java.util.List;

import org.apache.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.hizone.request.post.DeletePost;
import com.example.hizone.request.post.UploadPost;
import com.example.hizone.response.PostDetail;
import com.example.service.PostService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/post")
@CrossOrigin
@RestController
class PostController {

    @Autowired
    private PostService postService;

    /**
     * 上传帖子
     * 高频高精数据-插入数据库
     */
    @GlobalTransactional
    @PostMapping("/uploadPost")
    public String uploadPost(@RequestBody UploadPost uploadPost) {
        postService.uploadPost(uploadPost);
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
    public String deletePost(@RequestHeader("Token") String token, @RequestBody DeletePost deletePost) {
        postService.deletePost(deletePost.getPostId());
        return "success";
    }

    /**
     * 获取单个帖子
     * 高频高精数据-缓存
     * 
     */
    @GetMapping("/getPost")
    public PostDetail getPost(@RequestHeader(value = "Token", required = false) String token, @RequestParam("post_id") Long postId) {
        System.out.println("getPost");
        return postService.getPost(postId, token);
    }

    /**
     * 获取推送
     * 算法
     * 
     */
    @GetMapping("/getPush")
    public List<PostDetail> getPush(@RequestHeader(value = "Token", required = false) String token) {
        System.out.println("getPush");
        return postService.getPush(token);
    }
}