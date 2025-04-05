package com.example.hizone.outer;

import lombok.Data;

@Data
public class CommentDetail {

    private int postCommentId;

    private int postId;

    private int senderId;

    private String commentContent;

    private int commentLikeCount;

    private int commentReplyCount;

    private String commentTime;

    private boolean isLiked;
}
