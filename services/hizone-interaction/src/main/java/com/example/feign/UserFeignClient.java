package com.example.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.hizone.inter.UpdateUserMetadata;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@FeignClient(name = "hizone-user", url = "http://localhost:8081")
public interface UserFeignClient {

    @PostMapping("/updateUserMetadata")
    public String updateUserMetadata(@RequestBody UpdateUserMetadata updateUserMetadata);
}
