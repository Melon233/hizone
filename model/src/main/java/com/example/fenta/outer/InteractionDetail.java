package com.example.fenta.outer;

import lombok.Data;

@Data
public class InteractionDetail {

    private int postId;

    private int likeCount;

    private int collectCount;

    private int commentCount;

    private boolean isLiked;

    private boolean isCollected;
}
