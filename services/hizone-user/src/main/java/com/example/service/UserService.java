package com.example.service;

import java.util.List;

import com.example.hizone.dao.user.User;
import com.example.hizone.dao.user.UserMetadata;
import com.example.hizone.front.user.UpdateUserInfo;
import com.example.hizone.inter.UpdateUserMetadata;
import com.example.hizone.outer.UserDetail;

public interface UserService {

    User getUser(int userId);

    UserMetadata getUserMetadata(int userId);

    UserDetail getUserDetail(int userId);

    List<User> getAllUser();

    void updateUserInfo(UpdateUserInfo updateUser);

    void updateUserMetadata(UpdateUserMetadata updateUserMetadata);
}
