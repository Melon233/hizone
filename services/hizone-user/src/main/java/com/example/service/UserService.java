package com.example.service;

import java.util.List;

import com.example.fenta.dao.user.User;
import com.example.fenta.dao.user.UserDetail;
import com.example.fenta.dao.user.UserMetadata;
import com.example.fenta.front.user.UpdateUser;
import com.example.fenta.inter.UpdateUserMetadata;

public interface UserService {

    User getUser(int userId);

    UserMetadata getUserMetadata(int userId);

    UserDetail getUserDetail(int userId);

    List<User> getAllUser();

    void updateUser(UpdateUser updateUser);

    void updateUserMetadata(UpdateUserMetadata updateUserMetadata);
}
