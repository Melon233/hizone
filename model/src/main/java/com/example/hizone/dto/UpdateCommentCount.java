package com.example.hizone.dto;

import lombok.Data;

@Data
public class UpdateCommentCount {

    private Long postId;

    private Long increment;
}
