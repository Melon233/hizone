package com.example.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.fenta.inter.UpdateCommentCount;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@FeignClient(value = "hizone-interaction")
public interface InteractionFeignClient {

    @GetMapping("/updateCommentCount")
    public String updateCommentCount(@RequestBody UpdateCommentCount updateCommentCount);
}
