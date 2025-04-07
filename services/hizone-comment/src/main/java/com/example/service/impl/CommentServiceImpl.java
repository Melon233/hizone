package com.example.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.hizone.dao.comment.CommentLike;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
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
import com.example.mapper.CommentMapper;
import com.example.service.CommentService;

@Transactional
@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @SentinelResource(value = "getCommentList")
    @Override
    public List<Comment> getCommentList(int postId) {
        return commentMapper.selectCommentList(postId);
    }

    @SentinelResource(value = "getReplyList")
    @Override
    public List<Reply> getReplyList(int commentId) {
        return commentMapper.selectReplyList(commentId);
    }

    @SentinelResource(value = "addComment")
    @Override
    public void addComment(SendComment sendComment) {
        commentMapper.insertComment(sendComment);
    }

    @SentinelResource(value = "addReply")
    @Override
    public void addReply(ReplyComment replyComment) {
        commentMapper.insertReplyComment(replyComment);
        commentMapper.updateReplyCountIncrement(replyComment);
    }

    @SentinelResource(value = "addLikeComment")
    @Override
    public void addLikeComment(LikeComment likeComment) {
        commentMapper.insertCommentLike(likeComment);
        commentMapper.updateCommentLikeCountIncrement(likeComment);
    }

    @SentinelResource(value = "addLikeReply")
    @Override
    public void addLikeReply(LikeReply likeReply) {
        commentMapper.insertCommentReplyLike(likeReply);
        commentMapper.updateReplyLikeCountIncrement(likeReply);
    }

    @SentinelResource(value = "deleteComment")
    @Override
    public void deleteComment(DeleteComment deleteComment) {
        commentMapper.deleteComment(deleteComment);
    }

    @SentinelResource(value = "deleteReply")
    @Override
    public void deleteReply(DeleteReply deleteReply) {
        commentMapper.deleteReply(deleteReply);
    }

    @SentinelResource(value = "cancelLikeComment")
    @Override
    public void cancelLikeComment(CancelLikeComment cancelLikeComment) {
        commentMapper.cancelLikeComment(cancelLikeComment);
        commentMapper.updateCommentLikeCountDecrement(cancelLikeComment);
    }

    @SentinelResource(value = "cancelLikeReply")
    @Override
    public void cancelLikeReply(CancelLikeReply cancelLikeReply) {
        commentMapper.cancelLikeReply(cancelLikeReply);
        commentMapper.updateReplyLikeCountDecrement(cancelLikeReply);
    }

    @SentinelResource(value = "getCommentLikeList")
    @Override
    public List<CommentLike> getCommentLikeList(int postId) {
        return commentMapper.selectCommentLikeList(postId);
    }

    @Override
    public List<ReplyLike> getReplyLikeList(int parentCommentId) {
        return commentMapper.selectReplyLikeList(parentCommentId);
    }
}
