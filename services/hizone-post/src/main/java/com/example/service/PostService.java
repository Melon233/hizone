package com.example.service;

import java.util.List;

import com.example.hizone.request.post.UploadPost;
import com.example.hizone.response.PostDetail;

public interface PostService {

    void uploadPost(UploadPost uploadPost);

    void deletePost(Long postId);

    PostDetail getPost(Long postId, String token);

    List<PostDetail> getPush(String token);
}
