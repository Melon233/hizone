package com.example.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.hizone.dao.user.User;
import com.example.hizone.dao.user.UserMetadata;
import com.example.hizone.front.user.UpdateUser;
import com.example.hizone.inter.UpdateUserMetadata;
import com.example.hizone.outer.UserDetail;
import com.example.mapper.UserMapper;
import com.example.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public void updateUser(UpdateUser updateUser) {
        userMapper.updateUser(updateUser);
    }

    @Override
    public User getUser(int userId) {
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
    public UserMetadata getUserMetadata(int userId) {
        return userMapper.getUserMetadata(userId);
    }

    @Override
    public UserDetail getUserDetail(int userId) {
        return userMapper.getUserDetail(userId);
    }
}
