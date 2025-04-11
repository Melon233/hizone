package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class FollowApplication {

    public static void main(String[] args) {
        SpringApplication.run(FollowApplication.class, args);
    }
}