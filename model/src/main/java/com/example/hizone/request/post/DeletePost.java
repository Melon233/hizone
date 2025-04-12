package com.example.hizone.request.post;

import lombok.Data;

@Data
public class DeletePost {

    private Long authorId;

    private Long postId;
}
