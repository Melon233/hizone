package com.example.fenta.front.comment;

import lombok.Data;

@Data
public class LikeReply {

    private int senderId;

    private int parentCommentId;

    private int commentReplyId;
}
