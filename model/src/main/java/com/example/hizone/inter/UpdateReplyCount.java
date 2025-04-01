package com.example.hizone.inter;

import lombok.Data;

@Data
public class UpdateReplyCount {

    private int parentCommentId;

    private int replyCount;
}
