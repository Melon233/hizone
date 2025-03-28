package com.example.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.fenta.inter.UpdateUserMetadata;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@FeignClient(value = "hizone-user")
public interface UserFeignClient {

    @PostMapping("/updateUserMetadata")
    public void updateUserMetadata(@RequestBody UpdateUserMetadata updateUserMetadata);
}
