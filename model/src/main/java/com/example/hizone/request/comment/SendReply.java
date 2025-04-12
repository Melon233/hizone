package com.example.hizone.request.comment;

import lombok.Data;

@Data
public class SendReply {

    private Long postId;

    private Long senderId;

    private Long replyId;

    private String replyContent;

    private Long parentCommentId;

    private String replyTime;
}
