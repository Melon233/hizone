package com.example.hizone.request.interaction;

import lombok.Data;

@Data
public class CancelLikePost {

    private Long postId;

    private Long senderId;
}
