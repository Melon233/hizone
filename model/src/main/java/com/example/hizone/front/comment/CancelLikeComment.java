package com.example.hizone.front.comment;

import lombok.Data;

@Data
public class CancelLikeComment {

    private int postId;

    private int senderId;

    private int commentId;
}
