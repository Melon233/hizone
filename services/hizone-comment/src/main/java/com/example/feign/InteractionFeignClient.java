package com.example.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.hizone.inter.UpdateCommentCount;

@FeignClient(value = "hizone-interaction")
public interface InteractionFeignClient {

    @GetMapping("/updateCommentCount")
    public String updateCommentCount(@RequestBody UpdateCommentCount updateCommentCount);
}
