package com.example.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import com.example.hizone.dao.post.Post;
import com.example.hizone.dao.user.User;
import com.example.service.PostSearchService;
import com.example.service.UserSearchService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@CrossOrigin
@RestController
public class SearchController {

    @Autowired
    private UserSearchService userSearchService;

    @Autowired
    private PostSearchService postSearchService;

    @GetMapping("/searchUser")
    public List<User> searchUser(@RequestParam String keyword) throws IOException {
        return userSearchService.searchUser(keyword);
    }

    @PostMapping("/searchPost")
    public List<Post> searchPost(@RequestBody String keyword) throws IOException {
        return postSearchService.searchPost(keyword);
    }
}
