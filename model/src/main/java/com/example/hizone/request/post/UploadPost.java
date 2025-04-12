package com.example.hizone.request.post;

import lombok.Data;

@Data
public class UploadPost {

    private Long postId;

    private Long authorId;

    private String postTitle;

    private String postContent;
}
