package com.example.hizone.request.comment;

import lombok.Data;

@Data
public class SendComment {

    private Long postId;

    private Long senderId;

    private Long commentId;

    private String commentContent;

    private String commentTime;
}
