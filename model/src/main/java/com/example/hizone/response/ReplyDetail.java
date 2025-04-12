package com.example.hizone.response;

import lombok.Data;

@Data
public class ReplyDetail {

    private Long replyId;

    private Long postId;

    private Long senderId;

    private String senderName;

    private String replyContent;

    private Long parentCommentId;

    private Long replyLikeCount;

    private Boolean liked;

    private String replyTime;
}
