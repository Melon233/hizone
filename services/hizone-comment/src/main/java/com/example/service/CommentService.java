package com.example.service;

import java.util.List;

import com.example.hizone.dao.comment.CommentLike;
import com.example.hizone.dao.comment.Comment;
import com.example.hizone.dao.comment.Reply;
import com.example.hizone.dao.comment.ReplyLike;
import com.example.hizone.front.comment.CancelLikeComment;
import com.example.hizone.front.comment.CancelLikeReply;
import com.example.hizone.front.comment.DeleteComment;
import com.example.hizone.front.comment.DeleteReply;
import com.example.hizone.front.comment.LikeComment;
import com.example.hizone.front.comment.LikeReply;
import com.example.hizone.front.comment.ReplyComment;
import com.example.hizone.front.comment.SendComment;

public interface CommentService {

    void addComment(SendComment sendComment);

    void addReply(ReplyComment replyComment);

    List<Comment> getCommentList(int postId);

    List<Reply> getReplyList(int commentId);

    void addLikeComment(LikeComment likeComment);

    void addLikeReply(LikeReply likeReply);

    void deleteComment(DeleteComment deleteComment);

    void deleteReply(DeleteReply deleteReply);

    void cancelLikeComment(CancelLikeComment cancelLikeComment);

    void cancelLikeReply(CancelLikeReply cancelLikeReply);

    List<CommentLike> getCommentLikeList(int postId);

    List<ReplyLike> getReplyLikeList(int parentCommentId);
}
