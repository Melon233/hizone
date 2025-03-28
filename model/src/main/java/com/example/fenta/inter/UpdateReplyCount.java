package com.example.fenta.inter;

import lombok.Data;

@Data
public class UpdateReplyCount {

    private int parentCommentId;

    private int replyCount;
}
