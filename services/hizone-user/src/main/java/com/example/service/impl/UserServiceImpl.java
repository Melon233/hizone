package com.example.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.example.hizone.dto.UpdateUserMetadata;
import com.example.hizone.request.user.UpdateUserInfo;
import com.example.hizone.response.UserDetail;
import com.example.hizone.response.UserInfo;
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

    @SentinelResource(value = "updateUserInfo")
    @Override
    public void updateUserInfo(UpdateUserInfo updateUserInfo) {
        userMapper.updateUserInfo(updateUserInfo);
    }

    @Override
    public User getUser(Long userId) {
        return userMapper.getUser(userId);
    }

    @Override
    public List<User> getAllUser() {
        return userMapper.getAllUserList();
    }

    @Override
    public void updateUserMetadata(UpdateUserMetadata updateUserMetadata) {
        userMapper.updateUserMetadata(updateUserMetadata);
    }

    @Override
    public UserMetadata getUserMetadata(Long userId) {
        return userMapper.getUserMetadata(userId);
    }

    @Override
    public UserDetail getUserDetail(Long userId) {
        return userMapper.getUserDetail(userId);
    }

    @Override
    public List<UserInfo> getUserInfoList(List<Long> userIdList) {
        List<UserInfo> userInfoList = new ArrayList<>();
        //iterate userIdList
        for (Long userId : userIdList) {
            //from cache
            UserDetail userDetail = (UserDetail) userCacheService.getCache("user" + userId);
            //if not in cache
            if (userDetail == null) {
                //from db
                userDetail = getUserDetail(userId);
                //write to cache
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
