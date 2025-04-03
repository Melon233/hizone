package com.example.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.hizone.inter.UpdateCommentCount;

@FeignClient(name = "hizone-interaction", url = "http://localhost:8083/")
public interface InteractionFeignClient {

    @GetMapping("/updateCommentCount")
    public String updateCommentCount(@RequestBody UpdateCommentCount updateCommentCount);
}
