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

@FeignClient(value = "hizone-interaction")
public interface InteractionFeignClient {

    @GetMapping("/interaction/getInteractionDetail")
    public InteractionDetail getInteractionDetail(@RequestHeader(value = "Token", required = false) String token, @RequestParam("post_id") int postId);

    @PostMapping("/interaction/initInteraction")
    public String initInteraction(@RequestBody PostId postId);

    @GetMapping("/interaction/getInteractionDetailList")
    public List<InteractionDetail> getInteractionDetailList(@RequestHeader(value = "Token", required = false) String token, @RequestParam("post_id_list") int[] postIdList);
}
