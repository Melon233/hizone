package com.example.hizone.request.comment;

import lombok.Data;

@Data
public class CancelLikeReply {

    private Long postId;

    private Long senderId;

    private Long replyId;

    private Long parentCommentId;
}
