package com.example.hizone.table.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentLike {

    private Long postId;

    private Long senderId;

    private Long commentId;
}
