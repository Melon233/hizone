package com.example.hizone.request.comment;

import lombok.Data;

@Data
public class LikeComment {

    private Long postId;

    private Long senderId;

    private Long commentId;
}
