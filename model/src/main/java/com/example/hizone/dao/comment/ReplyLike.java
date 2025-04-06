package com.example.hizone.dao.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReplyLike {

    private int senderId;

    private int parentCommentId;

    private int replyId;
}
