package com.example.service;

import java.util.List;

import com.example.hizone.dao.comment.CommentLike;
import com.example.hizone.dao.comment.Comment;
import com.example.hizone.dao.comment.Reply;
import com.example.hizone.dao.comment.ReplyLike;
import com.example.hizone.front.comment.CancelLikeComment;
import com.example.hizone.front.comment.CancelLikeReply;
import com.example.hizone.front.comment.LikeComment;
import com.example.hizone.front.comment.LikeReply;
import com.example.hizone.inter.UpdateReplyCount;
import com.example.hizone.outer.CommentDetail;
import com.example.hizone.outer.ReplyDetail;

public interface CacheService {

    void setCache(String key, Object value);

    Object getCache(String key);

    List<CommentDetail> getCommentShardByScore(int postId, int userId, int start, int end);

    List<Comment> getCommentShardByTime(String key, int start, int end);

    List<ReplyDetail> getReplyShardByScore(int parentCommentId, int userId, int start, int end);

    void addComment(Comment comment);

    void addReply(Reply reply);

    void updateReplyCount(String key, UpdateReplyCount updateReplyCount);

    void likeComment(LikeComment likeComment);

    void likeReply(LikeReply likeReply);

    void appendCommentShard(int postId, List<Comment> commentList, List<CommentLike> commentLikeList);

    void appendReplyShard(int postId, List<Reply> replyList, List<ReplyLike> replyLikeList);

    void deleteComment(String key);

    void deleteReply(String key);

    void cancelLikeComment(CancelLikeComment cancelLikeComment);

    void cancelLikeReply(CancelLikeReply cancelLikeReply);
}
