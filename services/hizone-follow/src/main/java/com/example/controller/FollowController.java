package com.example.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.feign.UserFeignClient;
import com.example.hizone.dto.UpdateUserMetadata;
import com.example.hizone.request.follow.AddFollow;
import com.example.hizone.request.follow.DeleteFan;
import com.example.hizone.request.follow.DeleteFollow;
import com.example.hizone.table.follow.Follow;
import com.example.service.FollowService;

import java.util.List;

import org.apache.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/follow")
@GlobalTransactional
@CrossOrigin
@RestController
public class FollowController {

    @Autowired
    private FollowService followService;

    @Autowired
    private UserFeignClient userFeignClient;

    @PostMapping("/hasFollow")
    public boolean hasFollow(@RequestBody Follow follow) {
        return followService.hasFollow(follow);
    }

    /**
     * 获取粉丝列表
     * 低频高精数据-不缓存
     * 
     * @param userId
     * @return
     */
    @GetMapping("/getFanList")
    public List<Follow> getFanList(@RequestParam int userId) {
        return followService.getFanList(userId);
    }

    /**
     * 获取关注列表
     * 低频高精数据-不缓存
     * 
     * @param userId
     * @return
     */
    @GetMapping("/getFollowList")
    public List<Follow> getFollow(@RequestParam int userId) {
        return followService.getFollowList(userId);
    }

    /**
     * 添加关注
     * 低频高精数据
     * 
     * @param addFollow
     * @return
     */
    @PostMapping("/addFollow")
    public String addFollow(@RequestBody AddFollow addFollow) {
        followService.addFollow(addFollow);
        UpdateUserMetadata updateUserMetadata;
        updateUserMetadata = new UpdateUserMetadata();
        updateUserMetadata.setUserId(addFollow.getFollowerId());
        updateUserMetadata.setType("FollowCount");
        updateUserMetadata.setIsIncrement(true);
        ;
        userFeignClient.updateUserMetadata(updateUserMetadata);
        updateUserMetadata = new UpdateUserMetadata();
        updateUserMetadata.setUserId(addFollow.getFolloweeId());
        updateUserMetadata.setType("FanCount");
        updateUserMetadata.setIsIncrement(true);
        userFeignClient.updateUserMetadata(updateUserMetadata);
        return "success";
    }

    /**
     * 删除粉丝
     * 低频高精数据
     * 
     * @param deleteFan
     * @return
     */
    @PostMapping("/deleteFan")
    public String deleteFan(@RequestBody DeleteFan deleteFan) {
        followService.deleteFan(deleteFan);
        UpdateUserMetadata updateUserMetadata;
        updateUserMetadata = new UpdateUserMetadata();
        updateUserMetadata.setUserId(deleteFan.getFollowerId());
        updateUserMetadata.setType("FollowCount");
        updateUserMetadata.setIsIncrement(false);
        userFeignClient.updateUserMetadata(updateUserMetadata);
        updateUserMetadata = new UpdateUserMetadata();
        updateUserMetadata.setUserId(deleteFan.getFolloweeId());
        updateUserMetadata.setType("FanCount");
        updateUserMetadata.setIsIncrement(false);
        userFeignClient.updateUserMetadata(updateUserMetadata);
        return "success";
    }

    /**
     * 取消关注
     * 低频高精数据
     * 
     * @param deleteFollow
     * @return
     */
    @PostMapping("/deleteFollow")
    public String updateFollow(@RequestBody DeleteFollow deleteFollow) {
        followService.deleteFollow(deleteFollow);
        UpdateUserMetadata updateUserMetadata;
        updateUserMetadata = new UpdateUserMetadata();
        updateUserMetadata.setUserId(deleteFollow.getFollowerId());
        updateUserMetadata.setType("FollowCount");
        updateUserMetadata.setIsIncrement(false);
        userFeignClient.updateUserMetadata(updateUserMetadata);
        updateUserMetadata = new UpdateUserMetadata();
        updateUserMetadata.setUserId(deleteFollow.getFolloweeId());
        updateUserMetadata.setType("FanCount");
        updateUserMetadata.setIsIncrement(false);
        userFeignClient.updateUserMetadata(updateUserMetadata);
        return "success";
    }
}
