package com.example.fenta.dao.comment;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class PostReply {

    private int postId;

    private int senderId;

    private int commentReplyId;

    private String replyContent;

    private int parentCommentId;

    private int replyLikeCount;

    private LocalDateTime replyTime;
}
