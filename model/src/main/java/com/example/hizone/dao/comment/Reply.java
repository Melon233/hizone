package com.example.hizone.dao.comment;

import lombok.Data;

@Data
public class Reply {

    private int postId;

    private int senderId;

    private int replyId;

    private String replyContent;

    private int parentCommentId;

    private int replyLikeCount;

    private String replyTime;
}
