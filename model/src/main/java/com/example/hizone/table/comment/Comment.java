package com.example.hizone.table.comment;

import lombok.Data;

@Data
public class Comment {

    private Long commentId;

    private Long postId;

    private Long senderId;

    private String senderName;

    private String commentContent;

    private Long commentLikeCount;

    private Long replyCount;

    private String commentTime;
}
