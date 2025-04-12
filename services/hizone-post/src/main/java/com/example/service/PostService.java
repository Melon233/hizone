package com.example.service;

import java.util.List;

import com.example.hizone.request.post.ModifyPost;
import com.example.hizone.request.post.UploadPost;
import com.example.hizone.table.post.Post;

public interface PostService {

    void insertPost(UploadPost uploadPost);

    void deletePost(Long postId);

    Post getPost(Long postId);

    void modifyPost(ModifyPost modifyPost);

    List<Post> getPostList(Long authorId);

    List<Post> getPush();
}
