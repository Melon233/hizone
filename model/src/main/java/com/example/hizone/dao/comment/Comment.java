package com.example.hizone.dao.comment;

import lombok.Data;

@Data
public class Comment {

    private int commentId;

    private int postId;

    private int senderId;

    private String commentContent;

    private int commentLikeCount;

    private int replyCount;

    private String commentTime;
}
