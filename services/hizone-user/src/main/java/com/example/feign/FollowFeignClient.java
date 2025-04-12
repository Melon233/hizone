package com.example.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.hizone.table.follow.Follow;

@FeignClient(value = "hizone-follow")
public interface FollowFeignClient {

    @PostMapping("/follow/hasFollow")
    public boolean hasFollow(Follow follow);
}
