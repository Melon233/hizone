package com.example.service;

import java.io.IOException;
import java.util.List;

import com.example.hizone.table.user.User;

public interface UserSearchService {

    void syncUser() throws IOException;

    List<User> searchUser(String keyword) throws IOException;
}
