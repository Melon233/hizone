package com.example.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.hizone.dao.interaction.Interaction;
import com.example.hizone.inter.PostId;
import com.example.hizone.outer.InteractionDetail;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@FeignClient(name = "hizone-interaction", url = "http://localhost:8083")
public interface InteractionFeignClient {

    @GetMapping("/getInteractionDetail")
    public InteractionDetail getInteractionDetail(@RequestHeader(value = "Cookie") String token, @RequestParam("post_id") int postId);

    @PostMapping("/initInteraction")
    public String initInteraction(@RequestBody PostId postId);
}
