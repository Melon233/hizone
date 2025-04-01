package com.example.hizone.dao.comment;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class PostComment {

    private int commentId;

    private int postId;

    private int senderId;

    private String commentContent;

    private int commentLikeCount;

    private int commentReplyCount;

    private LocalDateTime commentTime;
}
