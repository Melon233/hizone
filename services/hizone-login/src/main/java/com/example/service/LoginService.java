package com.example.service;

import com.example.fenta.front.login.Login;

public interface LoginService {
    boolean validateLogin(Login login);

    void sendCheckCode(String email);

    int getUserIdByEmail(String email);
}
