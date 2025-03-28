package com.example.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.example.fenta.dao.comment.PostComment;
import com.example.fenta.dao.comment.PostReply;
import com.example.fenta.front.comment.LikeComment;
import com.example.fenta.front.comment.LikeReply;
import com.example.fenta.inter.UpdateReplyCount;
import com.example.fenta.utility.RedisUtility;
import com.example.service.CacheService;
import com.example.service.CommentService;
import com.github.benmanes.caffeine.cache.Cache;

public class CacheServiceImpl implements CacheService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private Cache<String, Object> caffeineCache;

    @Autowired
    private Cache<String, List<PostComment>> caffeineCacheCommentList;

    @Autowired
    private Cache<String, List<PostReply>> caffeineCacheReplyList;

    @Autowired
    private CommentService commentService;

    @Override
    public void setCache(String key, Object value) {
        caffeineCache.put(key, value);
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public Object getCache(String key) {
        Object cache;
        cache = caffeineCache.getIfPresent(key);
        if (cache != null) {
            return cache;
        }
        cache = redisTemplate.opsForValue().get(key);
        if (cache != null) {
            caffeineCache.put(key, cache);
            return cache;
        }
        return cache;
    }

    @Override
    public void addComment(String key, PostComment comment) {
        List<PostComment> postCommentList;
        if (caffeineCacheCommentList.getIfPresent(key) == null) {
            if (!redisTemplate.hasKey(key)) {
                postCommentList = commentService.getCommentList(comment.getPostId());
                postCommentList.add(comment);
                for (PostComment postComment : postCommentList) {
                    redisTemplate.opsForZSet().add(key, postComment, postComment.getCommentLikeCount());
                }
                postCommentList.sort(Comparator.comparing(PostComment::getCommentLikeCount));
                caffeineCacheCommentList.put(key, postCommentList);
                return;
            }
        }
        redisTemplate.opsForZSet().add(key, comment, comment.getCommentLikeCount());
        postCommentList = getCommentShardByScore(key, 0, -1);
        caffeineCacheCommentList.put(key, postCommentList);
    }

    @Override
    public void addReply(String key, PostReply reply) {
        List<PostReply> postReplyList;
        if (caffeineCacheReplyList.getIfPresent(key) == null) {
            if (!redisTemplate.hasKey(key)) {
                postReplyList = commentService.getReplyList(reply.getParentCommentId());
                postReplyList.add(reply);
                for (PostReply postReply : postReplyList) {
                    redisTemplate.opsForZSet().add(key, postReply, postReply.getReplyLikeCount());
                }
                postReplyList.sort(Comparator.comparing(PostReply::getReplyLikeCount));
                caffeineCacheReplyList.put(key, postReplyList);
                return;
            }
        }
        redisTemplate.opsForZSet().add(key, reply, reply.getReplyLikeCount());
        postReplyList = getReplyShardByScore(key, 0, -1);
        caffeineCacheReplyList.put(key, postReplyList);
    }

    @Override
    public void likeComment(String key, LikeComment likeComment) {
        redisTemplate.opsForZSet().incrementScore(key, likeComment.getCommentId(), 1);
        List<PostComment> commentList = caffeineCacheCommentList.getIfPresent(key);
        for (PostComment comment : commentList) {
            if (comment.getCommentId() == likeComment.getCommentId()) {
                comment.setCommentLikeCount(comment.getCommentLikeCount() + 1);
                break;
            }
            commentList.sort(Comparator.comparing(PostComment::getCommentLikeCount));
        }
    }

    @Override
    public void likeReply(String key, LikeReply likeReply) {
        redisTemplate.opsForZSet().incrementScore(key, likeReply.getCommentReplyId(), 1);
        List<PostReply> commentList = caffeineCacheReplyList.getIfPresent(key);
        for (PostReply comment : commentList) {
            if (comment.getCommentReplyId() == likeReply.getCommentReplyId()) {
                comment.setReplyLikeCount(comment.getReplyLikeCount() + 1);
                break;
            }
            commentList.sort(Comparator.comparing(PostReply::getReplyLikeCount));
        }
    }

    @Override
    public void appendCommentShard(String key, List<PostComment> commentList) {
        commentList.sort(Comparator.comparing(PostComment::getCommentLikeCount));
        redisTemplate.opsForZSet().add(key, RedisUtility.convertCommentListToTypedTupleListByScore(commentList));
        caffeineCacheCommentList.put(key, commentList);
    }

    @Override
    public void appendReplyShard(String key, List<PostReply> postReplyList) {
        postReplyList.sort(Comparator.comparing(PostReply::getReplyLikeCount));
        redisTemplate.opsForZSet().add(key, RedisUtility.convertReplyListToTypedTupleListByScore(postReplyList));
        caffeineCacheReplyList.put(key, postReplyList);
    }

    @Override
    public List<PostComment> getCommentShardByScore(String key, int start, int end) {
        List<PostComment> commentList;
        commentList = caffeineCacheCommentList.getIfPresent(key);
        if (commentList != null) {
            return commentList;
        }
        Set<Object> tupleSet = redisTemplate.opsForZSet().range(key, start, end);
        if (tupleSet != null) {
            commentList = new ArrayList<>();
            for (Object tuple : tupleSet) {
                commentList.add((PostComment) tuple);
            }
            return commentList;
        }
        return null;
    }

    public List<PostReply> getReplyShardByScore(String key, int start, int end) {
        List<PostReply> replyList;
        replyList = caffeineCacheReplyList.getIfPresent(key);
        if (replyList != null) {
            return replyList;
        }
        Set<Object> tupleSet = redisTemplate.opsForZSet().range(key, start, end);
        if (tupleSet != null) {
            replyList = new ArrayList<>();
            for (Object tuple : tupleSet) {
                replyList.add((PostReply) tuple);
            }
            return replyList;
        }
        return null;
    }

    @Override
    public List<PostComment> getCommentShardByTime(String key, int start, int end) {
        List<PostComment> commentList;
        commentList = caffeineCacheCommentList.getIfPresent(key);
        if (commentList != null) {
            return commentList;
        }
        Set<Object> tupleSet = redisTemplate.opsForZSet().range(key, start, end);
        if (tupleSet != null) {
            commentList = new ArrayList<>();
            for (Object tuple : tupleSet) {
                commentList.add((PostComment) tuple);
            }
            return commentList;
        }
        return commentList;
    }

    @Override
    public void updateReplyCount(String key, UpdateReplyCount updateReplyCount) {
        redisTemplate.opsForZSet().incrementScore(key, updateReplyCount.getParentCommentId(), updateReplyCount.getReplyCount());
        List<PostComment> commentList = caffeineCacheCommentList.getIfPresent(key);
        for (PostComment comment : commentList) {
            if (comment.getCommentId() == updateReplyCount.getParentCommentId()) {
                comment.setCommentLikeCount(comment.getCommentLikeCount() + updateReplyCount.getReplyCount());
                break;
            }
            commentList.sort(Comparator.comparing(PostComment::getCommentLikeCount));
        }
    }

    @Override
    public void deleteComment(String key) {
        redisTemplate.delete(key);
        caffeineCacheCommentList.invalidate(key);
    }

    @Override
    public void deleteReply(String key) {
        redisTemplate.delete(key);
        caffeineCacheReplyList.invalidate(key);
    }

    @Override
    public void cancelLikeComment(String key, int commentId) {
        redisTemplate.opsForZSet().incrementScore(key, commentId, -1);
        List<PostComment> commentList = caffeineCacheCommentList.getIfPresent(key);
        for (PostComment comment : commentList) {
            if (comment.getCommentId() == commentId) {
                comment.setCommentLikeCount(comment.getCommentLikeCount() - 1);
                break;
            }
            commentList.sort(Comparator.comparing(PostComment::getCommentLikeCount));
        }
    }

    @Override
    public void cancelLikeReply(String key, int commentReplyId) {
        redisTemplate.opsForZSet().incrementScore(key, commentReplyId, -1);
        List<PostReply> replyList = caffeineCacheReplyList.getIfPresent(key);
        for (PostReply reply : replyList) {
            if (reply.getCommentReplyId() == commentReplyId) {
                reply.setReplyLikeCount(reply.getReplyLikeCount() - 1);
                break;
            }
            replyList.sort(Comparator.comparing(PostReply::getReplyLikeCount));
        }
    }
}
