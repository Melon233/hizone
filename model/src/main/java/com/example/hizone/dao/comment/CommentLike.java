package com.example.hizone.dao.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentLike {

    private int postId;

    private int senderId;

    private int commentId;
}
