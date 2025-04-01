package com.example.hizone.front.comment;

import lombok.Data;

@Data
public class LikeReply {

    private int senderId;

    private int parentCommentId;

    private int commentReplyId;
}
