package com.example.hizone.dao.interaction;

import lombok.Data;

@Data
public class Interaction {

    private int postId;

    private int likeCount;

    private int collectCount;

    private int commentCount;
}
