package com.example.service;

import java.util.List;

import com.example.hizone.dao.post.Post;
import com.example.hizone.front.post.ModifyPost;
import com.example.hizone.front.post.UploadPost;

public interface PostService {

    void insertPost(UploadPost uploadPost);

    void deletePost(int postId);

    Post getPost(int postId);

    void modifyPost(ModifyPost modifyPost);

    List<Post> getPostList(int authorId);

    List<Post> getPush();
}
