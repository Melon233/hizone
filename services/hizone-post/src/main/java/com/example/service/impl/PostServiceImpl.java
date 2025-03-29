package com.example.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.fenta.dao.post.Post;
import com.example.fenta.front.post.ModifyPost;
import com.example.fenta.front.post.UploadPost;
import com.example.mapper.PostMapper;
import com.example.service.PostService;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostMapper postMapper;

    @Override
    public void insertPost(UploadPost uploadPost) {
        postMapper.insertPost(uploadPost);
    }

    @Override
    public void deletePost(int postId) {
        postMapper.deletePostById(postId);
    }

    @Override
    public Post getPost(int postId) {
        return postMapper.selectPostById(postId);
    }

    @Override
    public void modifyPost(ModifyPost modifyPost) {
        postMapper.updatePostById(modifyPost);
    }

    @Override
    public List<Post> getPostList(int authorId) {
        return postMapper.selectPostListByAuthorId(authorId);
    }

    @Override
    public List<Post> getPush() {
        return postMapper.selectPush();
    }
}
