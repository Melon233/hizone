package com.example.hizone.request.user;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class UpdateUserAvatar {

    private Long userId;

    private MultipartFile avatar;
}
