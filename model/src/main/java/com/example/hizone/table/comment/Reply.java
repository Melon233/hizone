package com.example.hizone.table.comment;

import lombok.Data;

@Data
public class Reply {

    private Long postId;

    private Long senderId;

    private Long replyId;

    private String replyContent;

    private Long parentCommentId;

    private Long replyLikeCount;

    private String replyTime;
}
