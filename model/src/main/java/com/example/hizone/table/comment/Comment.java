package com.example.hizone.table.comment;

import lombok.Data;

@Data
public class Comment {

    private Long commentId;

    private Long postId;

    private Long senderId;

    private String commentContent;

    private Long likeCount;

    private Long replyCount;

    private String commentTime;
}
