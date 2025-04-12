package com.example.hizone.request.comment;

import lombok.Data;

@Data
public class LikeReply {

    private Long postId;

    private Long senderId;

    private Long parentCommentId;

    private Long replyId;
}
