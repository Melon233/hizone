package com.example.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.component.CheckCodeManager;
import com.example.fenta.dao.user.User;
import com.example.fenta.front.login.Login;
import com.example.mapper.LoginMapper;
import com.example.service.LoginService;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private LoginMapper loginMapper;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private CheckCodeManager checkCodeManager;

    /**
     * 验证验证码是否正确
     * 不正确则返回假
     * 验证邮箱是否已注册
     * 未注册先注册
     * 最后返回真
     */
    @Override
    public boolean validateLogin(Login login) {
        if (!checkCodeManager.validateCheckCode(login)) {
            return false;
        }
        if (loginMapper.selectUserByEmail(login.getEmail()) == null) {
            User user = new User();
            user.setNickname(login.getEmail());
            user.setEmail(login.getEmail());
            loginMapper.insertUser(user);
            System.out.println("userId: " + user.getUserId());
            initUser(user.getUserId());
        }
        return true;
    }

    /**
     * 发送验证码到邮箱
     * 保存验证码
     */
    @Override
    public void sendCheckCode(String email) {
        SimpleMailMessage message = new SimpleMailMessage();
        int checkCode = checkCodeManager.generateAndSaveCheckCode(email);
        message.setFrom("2314795644@qq.com");
        message.setTo(email);
        message.setSubject("Hizone登录验证码");
        message.setText("你的登录验证码为：" + checkCode);
        mailSender.send(message);
    }

    @Override
    public int getUserIdByEmail(String email) {
        return loginMapper.selectUserByEmail(email).getUserId();
    }

    @Override
    public void initUser(int userId) {
        Path defaultAvatar = Paths.get("services/hizone-user/src/main/resources/avatar/default.png");
        Path userAvatar = Paths.get("services/hizone-user/src/main/resources/avatar/" + userId + ".png");
        try {
            Files.copy(defaultAvatar, userAvatar);
        } catch (IOException e) {
            e.printStackTrace();
        }
        loginMapper.insertNewUserMetadata(userId);
    }
}
