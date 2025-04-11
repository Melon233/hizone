package com.example.hizone.front.comment;

import lombok.Data;

@Data
public class DeleteReply {

    private int postId;

    private int senderId;

    private int parentCommentId;

    private int replyId;
}
