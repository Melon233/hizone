package com.example.service;

import java.util.List;

import com.example.hizone.request.comment.CancelLikeComment;
import com.example.hizone.request.comment.CancelLikeReply;
import com.example.hizone.request.comment.DeleteComment;
import com.example.hizone.request.comment.DeleteReply;
import com.example.hizone.request.comment.LikeComment;
import com.example.hizone.request.comment.LikeReply;
import com.example.hizone.request.comment.SendReply;
import com.example.hizone.request.comment.SendComment;
import com.example.hizone.table.comment.Comment;
import com.example.hizone.table.comment.CommentLike;
import com.example.hizone.table.comment.Reply;
import com.example.hizone.table.comment.ReplyLike;

public interface CommentService {

    void addComment(SendComment sendComment);

    void addReply(SendReply replyComment);

    List<Comment> getCommentList(Long postId);

    List<Reply> getReplyList(Long commentId);

    void addLikeComment(LikeComment likeComment);

    void addLikeReply(LikeReply likeReply);

    void deleteComment(DeleteComment deleteComment);

    void deleteReply(DeleteReply deleteReply);

    void cancelLikeComment(CancelLikeComment cancelLikeComment);

    void cancelLikeReply(CancelLikeReply cancelLikeReply);

    List<CommentLike> getCommentLikeList(Long postId);

    List<ReplyLike> getReplyLikeList(Long parentCommentId);
}
