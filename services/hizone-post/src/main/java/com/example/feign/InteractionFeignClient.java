package com.example.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.hizone.inter.PostId;
import com.example.hizone.outer.InteractionDetail;

@FeignClient(name = "hizone-interaction", url = "http://localhost:8083")
public interface InteractionFeignClient {

    @GetMapping("/getInteractionDetail")
    public InteractionDetail getInteractionDetail(@RequestHeader(value = "Token", required = false) String token, @RequestParam("post_id") int postId);

    @PostMapping("/initInteraction")
    public String initInteraction(@RequestBody PostId postId);

    @GetMapping("/getInteractionDetailList")
    public List<InteractionDetail> getInteractionDetailList(@RequestHeader(value = "Token", required = false) String token, @RequestParam("post_id_list") int[] postIdList);
}
