package com.example.hizone.front.comment;

import lombok.Data;

@Data
public class CancelLikeReply {

    private int postId;

    private int senderId;

    private int commentReplyId;

    private int parentCommentId;
}
