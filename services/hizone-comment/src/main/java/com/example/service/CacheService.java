package com.example.service;

import java.util.List;

import com.example.hizone.dao.comment.PostComment;
import com.example.hizone.dao.comment.PostReply;
import com.example.hizone.front.comment.LikeComment;
import com.example.hizone.front.comment.LikeReply;
import com.example.hizone.inter.UpdateReplyCount;

public interface CacheService {

    void setCache(String key, Object value);

    Object getCache(String key);

    List<PostComment> getCommentShardByScore(String key, int start, int end);

    List<PostComment> getCommentShardByTime(String key, int start, int end);

    List<PostReply> getReplyShardByScore(String key, int start, int end);

    void addComment(String key, PostComment comment);

    void addReply(String key, PostReply comment);

    void updateReplyCount(String key, UpdateReplyCount updateReplyCount);

    void likeComment(String key, LikeComment likeComment);

    void likeReply(String key, LikeReply likeReply);

    void appendCommentShard(String key, List<PostComment> commentList);

    void appendReplyShard(String string, List<PostReply> postReplyList);

    void deleteComment(String key);

    void deleteReply(String key);

    void cancelLikeComment(String key, int id);

    void cancelLikeReply(String key, int id);
}
