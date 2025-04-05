package com.example.hizone.dao.comment;

import lombok.Data;

@Data
public class PostReply {

    private int postId;

    private int senderId;

    private int commentReplyId;

    private String replyContent;

    private int parentCommentId;

    private int replyLikeCount;

    private String replyTime;
}
