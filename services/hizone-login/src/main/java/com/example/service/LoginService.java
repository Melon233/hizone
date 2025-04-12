package com.example.service;

import com.example.hizone.request.login.Login;

public interface LoginService {

    boolean validateLogin(Login login);

    void sendCheckCode(String email);

    Long getUserIdByEmail(String email);

    void initUser(Long userId); // 新增方法，用于初始化用户信息
}
