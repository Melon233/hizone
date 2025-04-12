package com.example.hizone.request.comment;

import lombok.Data;

@Data
public class DeleteComment {

    private Long postId;

    private Long senderId;

    private Long commentId;
}
