package com.example.service;

import java.io.IOException;
import java.util.List;

import com.example.hizone.response.PostDetail;

public interface PostSearchService {

    void syncPost() throws IOException;

    List<PostDetail> searchPost(String keyword) throws IOException;
}
