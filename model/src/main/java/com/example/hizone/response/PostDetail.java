package com.example.hizone.response;

import lombok.Data;

@Data
public class PostDetail {

    private Long postId;

    private Long authorId;

    private String authorName;

    private String postTitle;

    private String postContent;

    private Long likeCount;

    private Long collectCount;

    private Long commentCount;

    private String postTime;

    private Boolean liked;

    private Boolean collected;
}
