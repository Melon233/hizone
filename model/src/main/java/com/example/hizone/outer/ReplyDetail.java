package com.example.hizone.outer;

import lombok.Data;

@Data
public class ReplyDetail {

    private int postId;

    private int senderId;

    private String senderName;

    private int replyId;

    private String replyContent;

    private int parentCommentId;

    private int replyLikeCount;

    private boolean isLiked;

    private String replyTime;
}
