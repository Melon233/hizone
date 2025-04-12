package com.example.hizone.response;

import lombok.Data;

@Data
public class UserDetail {

    private Long userId;

    private String nickname;

    private String email;

    private String registerTime;

    private Boolean followed;

    private Long fanCount;

    private Long followCount;

    private Long postCount;

    private Long likedCount;

    private Long collectCount;
}
