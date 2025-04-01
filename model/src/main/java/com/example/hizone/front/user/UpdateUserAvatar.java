package com.example.hizone.front.user;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class UpdateUserAvatar {

    private int userId;

    private MultipartFile avatar;
}
