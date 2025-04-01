package com.example.service;

import java.util.List;

import com.example.hizone.dao.comment.PostComment;
import com.example.hizone.dao.comment.PostReply;
import com.example.hizone.front.comment.CancelLikeComment;
import com.example.hizone.front.comment.CancelLikeReply;
import com.example.hizone.front.comment.DeleteComment;
import com.example.hizone.front.comment.DeleteReply;
import com.example.hizone.front.comment.LikeComment;
import com.example.hizone.front.comment.LikeReply;
import com.example.hizone.front.comment.ReplyComment;
import com.example.hizone.front.comment.SendComment;

public interface CommentService {

    int addComment(SendComment sendComment);

    int addReply(ReplyComment replyComment);

    List<PostComment> getCommentList(int postId);

    List<PostReply> getReplyList(int commentId);

    void addLikeComment(LikeComment likeComment);

    void addLikeReply(LikeReply likeReply);

    void deleteComment(DeleteComment deleteComment);

    void deleteReply(DeleteReply deleteReply);

    void cancelLikeComment(CancelLikeComment cancelLikeComment);

    void cancelLikeReply(CancelLikeReply cancelLikeReply);
}
