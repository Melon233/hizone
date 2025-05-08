package com.example.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.example.feign.InteractionFeignClient;
import com.example.feign.UserFeignClient;
import com.example.hizone.dto.PostId;
import com.example.hizone.dto.UpdateUserMetadata;
import com.example.hizone.request.post.UploadPost;
import com.example.hizone.response.InteractionDetail;
import com.example.hizone.response.PostDetail;
import com.example.hizone.response.UserInfo;
import com.example.hizone.table.post.Post;
import com.example.mapper.PostMapper;
import com.example.service.PostService;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private UserFeignClient userFeignClient;

    @Autowired
    private InteractionFeignClient interactionFeignClient;

    @SentinelResource(value = "insertPost")
    @Override
    public void uploadPost(UploadPost uploadPost) {
        postMapper.insertPost(uploadPost);
        PostId postId = new PostId();
        postId.setPostId(uploadPost.getPostId());
        interactionFeignClient.initInteraction(postId);
        UpdateUserMetadata updateUserMetadata = new UpdateUserMetadata();
        updateUserMetadata.setUserId(uploadPost.getAuthorId());
        updateUserMetadata.setType("PostCount");
        updateUserMetadata.setIsIncrement(true);
        userFeignClient.updateUserMetadata(updateUserMetadata);
    }

    @SentinelResource(value = "deletePost")
    @Override
    public void deletePost(Long postId) {
        postMapper.deletePostById(postId);
    }

    @SentinelResource(value = "getPost")
    @Override
    public PostDetail getPost(Long postId, String token) {
        Post post = postMapper.selectPostById(postId);
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

    @SentinelResource(value = "getPush")
    @Override
    public List<PostDetail> getPush(String token) {
        List<Post> pushList = postMapper.selectPush();
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
}
