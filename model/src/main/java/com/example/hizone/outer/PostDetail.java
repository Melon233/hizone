package com.example.hizone.outer;

import java.util.Date;

import lombok.Data;

@Data
public class PostDetail {

    private int postId;

    private int authorId;

    private String authorName;

    private String postTitle;

    private String postContent;

    private int likeCount;

    private int collectCount;

    private int commentCount;

    private Date postTime;

    private boolean isLiked;

    private boolean isCollected;
}
