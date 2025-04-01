package com.example.hizone.dao.post;

import java.util.Date;

import lombok.Data;

@Data
public class Post {

    private int postId;

    private int authorId;

    private String authorName;

    private String postTitle;

    private String postContent;

    private Date postTime;
}
