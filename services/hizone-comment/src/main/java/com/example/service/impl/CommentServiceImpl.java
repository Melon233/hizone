package com.example.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

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
import com.example.mapper.CommentMapper;
import com.example.service.CommentService;

public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Override
    public List<PostComment> getCommentList(int postId) {
        return commentMapper.selectCommentList(postId);
    }

    @Override
    public List<PostReply> getReplyList(int commentId) {
        return commentMapper.selectReplyList(commentId);
    }

    @Override
    public int addComment(SendComment sendComment) {
        commentMapper.insertComment(sendComment);
        return sendComment.getCommentId();
    }

    @Override
    public int addReply(ReplyComment replyComment) {
        commentMapper.insertReplyComment(replyComment);
        return replyComment.getCommentReplyId();
    }

    @Override
    public void addLikeComment(LikeComment likeComment) {
        commentMapper.insertCommentLike(likeComment);
    }

    @Override
    public void addLikeReply(LikeReply likeReply) {
        commentMapper.insertCommentReplyLike(likeReply);
    }

    @Override
    public void deleteComment(DeleteComment deleteComment) {
        commentMapper.deleteComment(deleteComment);
    }

    @Override
    public void deleteReply(DeleteReply deleteReply) {
        commentMapper.deleteReply(deleteReply);
    }

    @Override
    public void cancelLikeComment(CancelLikeComment cancelLikeComment) {
        commentMapper.cancelLikeComment(cancelLikeComment);
    }

    @Override
    public void cancelLikeReply(CancelLikeReply cancelLikeReply) {
        commentMapper.cancelLikeReply(cancelLikeReply);
    }
}
