package com.example.fenta.front.post;

import lombok.Data;

@Data
public class UploadPost {

    private int authorId;

    private String postTitle;

    private String postContent;
}
