package com.example.hizone.request.comment;

import lombok.Data;

@Data
public class CancelLikeComment {

    private Long postId;

    private Long senderId;

    private Long commentId;
}
