package com.example.hizone.front.comment;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class SendComment {

    private int postId;

    private int senderId;

    private int commentId;

    private String commentContent;

    private LocalDateTime commentTime;
}
