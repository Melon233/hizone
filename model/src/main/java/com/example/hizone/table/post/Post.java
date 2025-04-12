package com.example.hizone.table.post;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Post {

    private Long postId;

    private Long authorId;

    private String postTitle;

    private String postContent;

    private LocalDateTime postTime;
}
