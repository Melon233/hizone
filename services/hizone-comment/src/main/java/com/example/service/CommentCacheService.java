package com.example.service;

import java.util.List;

import com.example.hizone.dto.UpdateReplyCount;
import com.example.hizone.request.comment.CancelLikeComment;
import com.example.hizone.request.comment.CancelLikeReply;
import com.example.hizone.request.comment.DeleteComment;
import com.example.hizone.request.comment.DeleteReply;
import com.example.hizone.request.comment.LikeComment;
import com.example.hizone.request.comment.LikeReply;
import com.example.hizone.response.CommentDetail;
import com.example.hizone.response.ReplyDetail;
import com.example.hizone.table.comment.Comment;
import com.example.hizone.table.comment.CommentLike;
import com.example.hizone.table.comment.Reply;
import com.example.hizone.table.comment.ReplyLike;

public interface CommentCacheService {

    void setCache(String key, Object value);

    Object getCache(String key);

    List<CommentDetail> getCommentShardByScore(Long postId, Long userId, Long start, Long end);

    List<Comment> getCommentShardByTime(String key, Long start, Long end);

    List<ReplyDetail> getReplyShardByScore(Long parentCommentId, Long userId, Long start, Long end);

    void addComment(Comment comment);

    void addReply(Reply reply);

    void updateReplyCount(String key, UpdateReplyCount updateReplyCount);

    void likeComment(LikeComment likeComment);

    void likeReply(LikeReply likeReply);

    void appendCommentShard(Long postId, List<Comment> commentList, List<CommentLike> commentLikeList);

    void appendReplyShard(Long postId, List<Reply> replyList, List<ReplyLike> replyLikeList);

    void deleteComment(DeleteComment deleteComment);

    void deleteReply(DeleteReply deleteReply);

    void cancelLikeComment(CancelLikeComment cancelLikeComment);

    void cancelLikeReply(CancelLikeReply cancelLikeReply);
}
