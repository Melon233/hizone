package com.example.service;

import com.example.hizone.front.login.Login;

public interface LoginService {

    boolean validateLogin(Login login);

    void sendCheckCode(String email);

    int getUserIdByEmail(String email);

    void initUser(int userId); // 新增方法，用于初始化用户信息
}
