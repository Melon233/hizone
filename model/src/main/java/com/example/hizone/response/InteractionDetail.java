package com.example.hizone.response;

import lombok.Data;

@Data
public class InteractionDetail {

    private Long postId;

    private Long likeCount;

    private Long collectCount;

    private Long commentCount;

    private boolean isLiked;

    private boolean isCollected;
}
