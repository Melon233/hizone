package com.example.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import com.example.hizone.outer.PostDetail;

@FeignClient(value = "hizone-post")
public interface PostFeignClient {

    @GetMapping("/post/getPush")
    List<PostDetail> getPush(@RequestHeader(value = "Token", required = false) String token);
}
