package com.example.fenta.dao.interaction;

import lombok.Data;

@Data
public class PostLike {

    private int postId;

    private int senderId;

    private int postLikeTime;
}
