package com.example.component;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.fenta.front.login.Login;
import com.example.fenta.inter.CheckCode;
import com.github.benmanes.caffeine.cache.Cache;

@Component
public class CheckCodeManager {

    @Autowired
    private Cache<String, CheckCode> checkCodeCache;

    public int generateAndSaveCheckCode(String email) {
        Random random=new Random();
        int code=random.nextInt(900000)+100000;
        CheckCode checkCode=new CheckCode();
        checkCode.setEmail(email);
        checkCode.setCheckCode(code);
        checkCode.setExpireTime(System.currentTimeMillis() + 1000 * 60 * 2);
        checkCodeCache.put(email, checkCode);
        return code;
    }

    public boolean validateCheckCode(Login login) {
        CheckCode checkCode = checkCodeCache.getIfPresent(login.getEmail());
        if (checkCode == null) {
            return false;
        }
        if (checkCode.getExpireTime() < System.currentTimeMillis()) {
            return false;
        }
        if (checkCode.getCheckCode() != Integer.parseInt(login.getCheckCode())) {
            return false;
        }
        checkCodeCache.invalidate(login.getEmail());
        return true;
    }

    public void clearExpireCheckCode() {
        checkCodeCache.asMap().entrySet().removeIf(entry -> entry.getValue().getExpireTime() < System.currentTimeMillis());
    }
}
