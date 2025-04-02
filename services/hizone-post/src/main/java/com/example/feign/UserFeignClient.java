package com.example.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.hizone.inter.UpdateUserMetadata;
import com.example.hizone.outer.UserInfo;

@FeignClient(name = "hizone-user", url = "http://localhost:8081")
public interface UserFeignClient {

    @GetMapping("/getUserInfoList")
    public List<UserInfo> getUserInfoList(@RequestParam("user_id_list") int[] userIdList);

    @PostMapping("/updateUserMetadata")
    public String updateUserMetadata(@RequestBody UpdateUserMetadata updateUserMetadata);
}
