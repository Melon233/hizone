package com.example.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.hizone.table.post.Post;

@FeignClient(value = "hizone-user")
public interface UserFeignClient {

    @GetMapping("/user/getAllUser")
    List<Post> getAllUser();
}
