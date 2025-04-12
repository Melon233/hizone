package com.example.hizone.table.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReplyLike {

    private Long senderId;

    private Long parentCommentId;

    private Long replyId;
}
