package com.example.service;

import java.io.IOException;
import java.util.List;

import com.example.hizone.dao.post.Post;

public interface PostSearchService {

    void syncPost() throws IOException;

    List<Post> searchPost(String keyword) throws IOException;
}
