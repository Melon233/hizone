package com.example.service;

import java.util.List;

import com.example.hizone.dto.UpdateUserMetadata;
import com.example.hizone.request.user.UpdateUserInfo;
import com.example.hizone.response.UserDetail;
import com.example.hizone.response.UserInfo;
import com.example.hizone.table.user.User;
import com.example.hizone.table.user.UserMetadata;

public interface UserService {

    User getUser(Long userId);

    UserMetadata getUserMetadata(Long userId);

    UserDetail getUserDetail(Long userId);

    List<User> getAllUser();

    void updateUserInfo(UpdateUserInfo updateUser);

    void updateUserMetadata(UpdateUserMetadata updateUserMetadata);

    List<UserInfo> getUserInfoList(List<Long> userIdList);
}
