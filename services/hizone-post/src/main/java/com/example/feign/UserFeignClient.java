package com.example.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.fenta.dao.user.User;
import com.example.fenta.inter.UpdateUserMetadata;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@FeignClient(name="hizone-user", url = "http://localhost:8081")
public interface UserFeignClient {

    @GetMapping("/getUser")
    public User getUser(@RequestParam("user_id") int userId);

    @PostMapping("/updateUserMetadata")
    public String updateUserMetadata(@RequestBody UpdateUserMetadata updateUserMetadata);
}
