package com.example.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.example.hizone.request.post.ModifyPost;
import com.example.hizone.request.post.UploadPost;
import com.example.hizone.table.post.Post;
import com.example.mapper.PostMapper;
import com.example.service.PostService;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostMapper postMapper;

    @SentinelResource(value = "insertPost")
    @Override
    public void insertPost(UploadPost uploadPost) {
        postMapper.insertPost(uploadPost);
    }

    @SentinelResource(value = "deletePost")
    @Override
    public void deletePost(Long postId) {
        postMapper.deletePostById(postId);
    }

    @SentinelResource(value = "getPost")
    @Override
    public Post getPost(Long postId) {
        return postMapper.selectPostById(postId);
    }

    @SentinelResource(value = "modifyPost")
    @Override
    public void modifyPost(ModifyPost modifyPost) {
        postMapper.updatePostById(modifyPost);
    }

    @SentinelResource(value = "getPostList")
    @Override
    public List<Post> getPostList(Long authorId) {
        return postMapper.selectPostListByAuthorId(authorId);
    }

    @SentinelResource(value = "getPush")
    @Override
    public List<Post> getPush() {
        return postMapper.selectPush();
    }
}
