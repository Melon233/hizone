package com.example.hizone.dto;

import lombok.Data;

@Data
public class UpdateReplyCount {

    private Long parentCommentId;

    private Long replyCount;
}
