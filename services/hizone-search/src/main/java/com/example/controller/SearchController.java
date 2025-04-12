package com.example.controller;

import java.io.IOException;
import java.util.List;

import org.apache.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import com.example.hizone.response.PostDetail;
import com.example.hizone.table.user.User;
import com.example.service.PostSearchService;
import com.example.service.UserSearchService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/search")
@GlobalTransactional
@CrossOrigin
@RestController
public class SearchController {

    @Autowired
    private UserSearchService userSearchService;

    @Autowired
    private PostSearchService postSearchService;

    @GetMapping("/searchUser")
    public List<User> searchUser(@RequestParam("keyword") String keyword) throws IOException {
        return userSearchService.searchUser(keyword);
    }

    @GetMapping("/searchPost")
    public List<PostDetail> searchPost(@RequestParam("keyword") String keyword) throws IOException {
        return postSearchService.searchPost(keyword);
    }
}
