package com.example.hizone.table.user;

import lombok.Data;

@Data
public class UserMetadata {

    private Long userId;

    private Long fanCount;

    private Long followCount;

    private Long collectCount;

    private Long postCount;

    private Long likedCount;
}
