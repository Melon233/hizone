package com.example.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.hizone.dao.post.Post;

@FeignClient(value = "hizone-user")
public interface UserFeignClient {

    @GetMapping("/getAllUser")
    List<Post> getAllUser();
}
