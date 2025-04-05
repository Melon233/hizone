package com.example.service;

import java.util.List;

import com.example.hizone.dao.comment.PostComment;
import com.example.hizone.dao.comment.PostReply;
import com.example.hizone.front.comment.CancelLikeComment;
import com.example.hizone.front.comment.CancelLikeReply;
import com.example.hizone.front.comment.LikeComment;
import com.example.hizone.front.comment.LikeReply;
import com.example.hizone.inter.UpdateReplyCount;

public interface CacheService {

    void setCache(String key, Object value);

    Object getCache(String key);

    List<PostComment> getCommentShardByScore(int postId, int start, int end);

    List<PostComment> getCommentShardByTime(String key, int start, int end);

    List<PostReply> getReplyShardByScore(int parentCommentId, int start, int end);

    void addComment(PostComment postComment);

    void addReply(PostReply postReply);

    void updateReplyCount(String key, UpdateReplyCount updateReplyCount);

    void likeComment(LikeComment likeComment);

    void likeReply(LikeReply likeReply);

    void appendCommentShard(int postId, List<PostComment> commentList);

    void appendReplyShard(int postId, List<PostReply> postReplyList);

    void deleteComment(String key);

    void deleteReply(String key);

    void cancelLikeComment(CancelLikeComment cancelLikeComment);

    void cancelLikeReply(CancelLikeReply cancelLikeReply);
}
