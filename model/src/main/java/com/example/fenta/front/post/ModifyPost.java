package com.example.fenta.front.post;

import lombok.Data;

@Data
public class ModifyPost {

    private int authorId;

    private int postId;

    private String postTitle;

    private String postContent;
}
