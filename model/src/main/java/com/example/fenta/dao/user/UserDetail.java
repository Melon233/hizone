package com.example.fenta.dao.user;

import lombok.Data;

@Data
public class UserDetail {

    private int userId;

    private String nickname;

    private String email;

    private String registerTime;

    private int fanCount;

    private int followCount;

    private int postCount;

    private int likedCount;

    private int collectCount;
}
