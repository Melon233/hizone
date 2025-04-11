package com.example.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.hizone.inter.UpdateUserMetadata;

@FeignClient(value = "hizone-user")
public interface UserFeignClient {

    @PostMapping("/user/updateUserMetadata")
    public void updateUserMetadata(@RequestBody UpdateUserMetadata updateUserMetadata);
}
