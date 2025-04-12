package com.example.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.example.feign.UserFeignClient;
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
import com.example.mapper.CommentMapper;
import com.example.service.CommentService;

@Transactional
@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private UserFeignClient userFeignClient;

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
    public void addReply(SendReply replyComment) {
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
        commentMapper.updateReplyCountDecrement(deleteReply);
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
