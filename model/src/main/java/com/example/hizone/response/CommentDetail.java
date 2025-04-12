package com.example.hizone.response;

import lombok.Data;

@Data
public class CommentDetail {

    private Long commentId;

    private Long postId;

    private Long senderId;

    private String senderName;

    private String commentContent;

    private Long commentLikeCount;

    private Long replyCount;

    private String commentTime;

    private Boolean liked;
}
