package com.example.service;

import java.util.List;

import com.example.fenta.dao.comment.PostComment;
import com.example.fenta.dao.comment.PostReply;
import com.example.fenta.front.comment.CancelLikeComment;
import com.example.fenta.front.comment.CancelLikeReply;
import com.example.fenta.front.comment.DeleteComment;
import com.example.fenta.front.comment.DeleteReply;
import com.example.fenta.front.comment.LikeComment;
import com.example.fenta.front.comment.LikeReply;
import com.example.fenta.front.comment.ReplyComment;
import com.example.fenta.front.comment.SendComment;

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
