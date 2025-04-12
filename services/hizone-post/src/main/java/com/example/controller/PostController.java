package com.example.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.feign.InteractionFeignClient;
import com.example.feign.UserFeignClient;
import com.example.hizone.dto.PostId;
import com.example.hizone.dto.UpdateUserMetadata;
import com.example.hizone.request.post.DeletePost;
import com.example.hizone.request.post.ModifyPost;
import com.example.hizone.request.post.UploadPost;
import com.example.hizone.response.InteractionDetail;
import com.example.hizone.response.PostDetail;
import com.example.hizone.response.UserInfo;
import com.example.hizone.table.post.Post;
import com.example.service.CacheService;
import com.example.service.PostService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/post")
@GlobalTransactional
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
    public PostDetail getPost(@RequestHeader(value = "Token", required = false) String token, @RequestParam("post_id") Long postId) {
        Post post = postService.getPost(postId);
        UserInfo userInfo = userFeignClient.getUserInfoList(List.of(post.getAuthorId())).get(0);
        InteractionDetail interactionDetail = interactionFeignClient.getInteractionDetailList(token, List.of(postId)).get(0);
        PostDetail postDetail = new PostDetail();
        postDetail.setPostId(post.getPostId());
        postDetail.setAuthorId(post.getAuthorId());
        postDetail.setAuthorName(userInfo.getNickname());
        postDetail.setPostTitle(post.getPostTitle());
        postDetail.setPostContent(post.getPostContent());
        postDetail.setLikeCount(interactionDetail.getLikeCount());
        postDetail.setCollectCount(interactionDetail.getCollectCount());
        postDetail.setCommentCount(interactionDetail.getCommentCount());
        postDetail.setPostTime(post.getPostTime());
        postDetail.setLiked(interactionDetail.isLiked());
        postDetail.setCollected(interactionDetail.isCollected());
        return postDetail;
    }

    /**
     * 获取我发布的帖子
     * 低频高精数据-不缓存
     * 
     * @param authorId
     * @return
     */
    @GetMapping("/getPostList")
    public List<Post> getPostList(@RequestParam Long authorId) {
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
    public List<PostDetail> getPush(@RequestHeader(value = "Token", required = false) String token) {
        System.out.println("getPush");
        List<Post> pushList = postService.getPush();
        List<UserInfo> userInfoList = userFeignClient.getUserInfoList(pushList.stream().mapToLong(Post::getAuthorId).boxed().toList());
        List<InteractionDetail> interactionDetailList = interactionFeignClient.getInteractionDetailList(token, pushList.stream().mapToLong(Post::getPostId).boxed().toList());
        List<PostDetail> postDetailList = new ArrayList<>();
        for (int i = 0; i < pushList.size(); i++) {
            PostDetail postDetail = new PostDetail();
            postDetail.setPostId(pushList.get(i).getPostId());
            postDetail.setAuthorId(pushList.get(i).getAuthorId());
            postDetail.setAuthorName(userInfoList.get(i).getNickname());
            postDetail.setPostTitle(pushList.get(i).getPostTitle());
            postDetail.setPostContent(pushList.get(i).getPostContent());
            postDetail.setLikeCount(interactionDetailList.get(i).getLikeCount());
            postDetail.setCollectCount(interactionDetailList.get(i).getCollectCount());
            postDetail.setCommentCount(interactionDetailList.get(i).getCommentCount());
            postDetail.setPostTime(pushList.get(i).getPostTime());
            postDetail.setLiked(interactionDetailList.get(i).isLiked());
            postDetail.setCollected(interactionDetailList.get(i).isCollected());
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
        updateUserMetadata.setPostCount(1L);
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
        updateUserMetadata.setPostCount(-1L);
        userFeignClient.updateUserMetadata(updateUserMetadata);
        return "success";
    }
}