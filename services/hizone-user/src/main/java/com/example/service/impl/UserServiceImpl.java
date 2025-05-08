package com.example.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.example.feign.FollowFeignClient;
import com.example.hizone.dto.UpdateUserMetadata;
import com.example.hizone.request.user.UpdateUserInfo;
import com.example.hizone.response.UserDetail;
import com.example.hizone.response.UserInfo;
import com.example.hizone.table.follow.Follow;
import com.example.hizone.table.user.User;
import com.example.hizone.table.user.UserMetadata;
import com.example.mapper.UserMapper;
import com.example.service.UserCacheService;
import com.example.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserCacheService userCacheService;

    @Autowired
    private FollowFeignClient followFeignClient;

    @Autowired
    private UserCacheService cacheService;

    @SentinelResource(value = "updateUserInfo")
    @Override
    public void updateUserInfo(UpdateUserInfo updateUserInfo) {
        userMapper.updateUserInfo(updateUserInfo);
    }

    @Override
    public User getUser(Long userId) {
        return userMapper.selectUser(userId);
    }

    @Override
    public List<User> getAllUser() {
        return userMapper.getAllUserList();
    }

    @Override
    public void updateUserMetadata(UpdateUserMetadata updateUserMetadata) {
        System.out.println("here");
        Long userId = updateUserMetadata.getUserId();
        String type = updateUserMetadata.getType();
        Integer increment = updateUserMetadata.getIsIncrement() ? 1 : -1;
        if (type.equals("FanCount")) {
            userMapper.updateFanCount(userId, increment);
        } else if (type.equals("FollowCount")) {
            userMapper.updateFollowCount(userId, increment);
        } else if (type.equals("CollectCount")) {
            userMapper.updateCollectCount(userId, increment);
        } else if (type.equals("PostCount")) {
            System.out.println("updatePostCount: " + updateUserMetadata);
            userMapper.updatePostCount(userId, increment);
        } else if (type.equals("LikedCount")) {
            userMapper.updateLikedCount(userId, increment);
        }
        cacheService.deleteCache("user" + updateUserMetadata.getUserId());
    }

    @Override
    public UserMetadata getUserMetadata(Long userId) {
        return userMapper.selectUserMetadata(userId);
    }

    @Override
    public UserDetail getUserDetail(Long selfId, Long userId) {
        UserDetail userDetail = (UserDetail) cacheService.getCache("user" + userId);
        if (userDetail == null) {
            UserMetadata userMetadata = getUserMetadata(userId);
            User user = getUser(userId);
            userDetail = new UserDetail();
            userDetail.setUserId(userId);
            userDetail.setNickname(user.getNickname());
            userDetail.setEmail(user.getEmail());
            userDetail.setRegisterTime(user.getRegisterTime());
            userDetail.setCollectCount(userMetadata.getCollectCount());
            userDetail.setFanCount(userMetadata.getFanCount());
            userDetail.setFollowCount(userMetadata.getFollowCount());
            userDetail.setLikedCount(userMetadata.getLikedCount());
            userDetail.setPostCount(userMetadata.getPostCount());
            cacheService.setCache("user" + userId, userDetail);
        }
        if (selfId != null) {
            userDetail.setFollowed(followFeignClient.hasFollow(new Follow(selfId, userId)));
        }
        return userDetail;
    }

    @Override
    public List<UserInfo> getUserInfoList(List<Long> userIdList) {
        List<UserInfo> userInfoList = new ArrayList<>();
        //iterate userIdList
        for (Long userId : userIdList) {
            //from cache
            System.out.println("userIds: " + userId);
            UserDetail userDetail = (UserDetail) userCacheService.getCache("user" + userId);
            //if not in cache
            if (userDetail == null) {
                //from db
                userDetail = getUserDetail(null, userId);
                //write to cache
                System.out.println(userDetail);
                userCacheService.setCache("user" + userId, userDetail);
            }
            //extract partial user info
            UserInfo userInfo = new UserInfo();
            userInfo.setUserId(userId);
            userInfo.setNickname(userDetail.getNickname());
            userInfoList.add(userInfo);
        }
        return userInfoList;
    }
}
