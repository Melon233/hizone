package com.example.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.hizone.inter.UpdateUserMetadata;

@RequestMapping("/user")
@FeignClient(value = "hizone-user")
public interface UserFeignClient {

    @PostMapping("/updateUserMetadata")
    public void updateUserMetadata(@RequestBody UpdateUserMetadata updateUserMetadata);
}
