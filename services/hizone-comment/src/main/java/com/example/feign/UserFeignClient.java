package com.example.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.hizone.outer.UserInfo;

@FeignClient(value = "hizone-user")
public interface UserFeignClient {

    @GetMapping("/user/getUserInfoList")
    public List<UserInfo> getUserInfoList(@RequestParam("user_id_list") int[] userIdList);
}
