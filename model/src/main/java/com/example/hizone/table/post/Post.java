package com.example.hizone.table.post;

import lombok.Data;

@Data
public class Post {

    private Long postId;

    private Long authorId;

    private String postTitle;

    private String postContent;

    private String postTime;
}
