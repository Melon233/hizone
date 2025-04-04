package com.example.hizone.dao.comment;

import lombok.Data;

@Data
public class PostComment {

    private int postCommentId;

    private int postId;

    private int senderId;

    private String commentContent;

    private int commentLikeCount;

    private int commentReplyCount;

    private String commentTime;
}
