package com.example.controller;

import org.apache.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.hizone.dto.Email;
import com.example.hizone.request.login.Login;
import com.example.hizone.utility.TokenUtility;
import com.example.service.LoginService;

@RequestMapping("/login")
@GlobalTransactional
@RestController
@CrossOrigin
public class LoginController {

    @Autowired
    private LoginService loginService;

    /**
     * 验证邮箱验证码是否正确
     * 正确则根据邮箱生成token返回
     * 否则返回错误消息
     */
    @PostMapping("/login")
    public String login(@RequestBody Login login) {
        if (!loginService.validateLogin(login)) {
            return "error";
        }
        return TokenUtility.generateToken(loginService.getUserIdByEmail(login.getEmail()));
    }

    /**
     * 验证邮箱格式是否正确
     * 查询邮箱是否存在
     * 不存在则创建邮箱账户
     * 最后生成随机六位验证码到邮箱
     */
    @PostMapping("/sendCheckCode")
    public String sendCheckCode(@RequestBody Email email) {
        if (!TokenUtility.validateEmail(email.getEmail())) {
            return "invalid email";
        }
        loginService.sendCheckCode(email.getEmail());
        return "success";
    }
}
