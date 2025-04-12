package com.example.hizone.dto;

import lombok.Data;

@Data
public class UpdateUserMetadata {

    private Long userId;

    private Long fanCount;

    private Long followCount;

    private Long collectCount;

    private Long postCount;

    private Long likedCount;
}
