package com.example.hizone.front.comment;

import lombok.Data;

@Data
public class ReplyComment {

    private int postId;

    private int senderId;

    private int replyId;

    private String replyContent;

    private int parentCommentId;

    private String replyTime;
}
