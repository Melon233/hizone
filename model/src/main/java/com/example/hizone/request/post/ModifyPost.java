package com.example.hizone.request.post;

import lombok.Data;

@Data
public class ModifyPost {

    private Long authorId;

    private Long postId;

    private String postTitle;

    private String postContent;
}
