package com.example.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import com.example.hizone.dao.comment.PostComment;
import com.example.hizone.dao.comment.PostReply;
import com.example.hizone.front.comment.CancelLikeComment;
import com.example.hizone.front.comment.CancelLikeReply;
import com.example.hizone.front.comment.LikeComment;
import com.example.hizone.front.comment.LikeReply;
import com.example.hizone.inter.UpdateReplyCount;
import com.example.service.CacheService;
import com.github.benmanes.caffeine.cache.Cache;

@Service
public class CacheServiceImpl implements CacheService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private Cache<String, Object> caffeineCache;

    @Autowired
    private Cache<Integer, TreeSet<PostComment>> caffeineCommentSet;

    @Autowired
    private Cache<Integer, TreeSet<PostReply>> caffeineReplySet;

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

    /**
     * 
     */
    @Override
    public void addComment(PostComment postComment) {
        redisTemplate.opsForHash().put("comment" + postComment.getPostId(), Integer.toString(postComment.getPostCommentId()), postComment);
        redisTemplate.opsForZSet().add("comment-score" + postComment.getPostId(), postComment.getPostCommentId(), postComment.getCommentLikeCount());
    }

    @Override
    public void addReply(PostReply postReply) {
        redisTemplate.opsForHash().put("reply" + postReply.getParentCommentId(), Integer.toString(postReply.getCommentReplyId()), postReply);
        redisTemplate.opsForZSet().add("reply-score" + postReply.getParentCommentId(), postReply.getCommentReplyId(), postReply.getReplyLikeCount());
    }

    /**
     * 修改hash评论的点赞数
     * 修改zset评论的分数
     * 
     * @param likeComment
     */
    @Override
    public void likeComment(LikeComment likeComment) {
        if (!redisTemplate.hasKey("comment" + likeComment.getPostId()) ||
                !redisTemplate.hasKey("comment-score" + likeComment.getPostId()) ||
                !redisTemplate.hasKey("comment-like" + likeComment.getCommentId())) {
            return;
        }
        PostComment postComment = (PostComment) redisTemplate.opsForHash().get("comment" + likeComment.getPostId(), likeComment.getCommentId());
        postComment.setCommentLikeCount(postComment.getCommentLikeCount() + 1);
        redisTemplate.opsForHash().put("comment" + likeComment.getPostId(), likeComment.getCommentId(), postComment);
        redisTemplate.opsForZSet().incrementScore("comment-score" + likeComment.getPostId(), likeComment.getCommentId(), 1);
        redisTemplate.opsForSet().add("comment-like" + likeComment.getCommentId(), likeComment.getSenderId());
    }

    @Override
    public void likeReply(LikeReply likeReply) {
        if (!redisTemplate.hasKey("reply" + likeReply.getParentCommentId()) ||
                !redisTemplate.hasKey("reply-score" + likeReply.getParentCommentId()) ||
                !redisTemplate.hasKey("reply-like" + likeReply.getCommentReplyId())) {
            return;
        }
        PostComment postComment = (PostComment) redisTemplate.opsForHash().get("reply" + likeReply.getParentCommentId(), likeReply.getCommentReplyId());
        postComment.setCommentLikeCount(postComment.getCommentLikeCount() + 1);
        redisTemplate.opsForHash().put("reply" + likeReply.getParentCommentId(), likeReply.getCommentReplyId(), postComment);
        redisTemplate.opsForZSet().incrementScore("reply-score" + likeReply.getParentCommentId(), likeReply.getCommentReplyId(), 1);
        redisTemplate.opsForSet().add("reply-like" + likeReply.getParentCommentId(), likeReply.getSenderId());
    }

    @Override
    public void appendCommentShard(int postId, List<PostComment> commentList) {
        Map<String, PostComment> commentMap = commentList.stream().collect(Collectors.toMap(postComment -> Integer.toString(postComment.getPostCommentId()), postComment -> postComment));
        redisTemplate.opsForHash().putAll("comment" + postId, commentMap);
        Set<ZSetOperations.TypedTuple<Object>> tuples = commentList.stream()
                .map(comment -> new DefaultTypedTuple<Object>(comment.getPostCommentId(), (double) comment.getCommentLikeCount()))
                .collect(Collectors.toSet());
        redisTemplate.opsForZSet().add("comment-score" + postId, tuples);
    }

    @Override
    public void appendReplyShard(int postId, List<PostReply> replyList) {
        Map<String, PostReply> replyMap = replyList.stream().collect(Collectors.toMap(reply -> Integer.toString(reply.getCommentReplyId()), reply -> reply));
        redisTemplate.opsForHash().putAll("reply" + postId, replyMap);
        Set<ZSetOperations.TypedTuple<Object>> tuples = replyList.stream()
                .map(reply -> new DefaultTypedTuple<Object>(reply.getCommentReplyId(), (double) reply.getReplyLikeCount()))
                .collect(Collectors.toSet());
        redisTemplate.opsForZSet().add("comment-score" + postId, tuples);
    }

    @Override
    public List<PostComment> getCommentShardByScore(int postId, int start, int end) {
        if (!redisTemplate.hasKey("comment-score" + postId) || !redisTemplate.hasKey("comment" + postId)) {
            return null;
        }
        Set<Object> tupleSet = redisTemplate.opsForZSet().range("comment-score" + postId, start, end)
                .stream()
                .map(item -> (Object) item.toString())
                .collect(Collectors.toSet());
        // Set<Object> collection = tupleSet
        // .stream()
        // .map(item -> (Object) item)
        // .collect(Collectors.toSet());
        return redisTemplate.opsForHash().multiGet("comment" + postId, tupleSet)
                .stream()
                .map(item -> (PostComment) item)
                .collect(Collectors.toList());
        // Set<Object> tupleSet = redisTemplate.opsForZSet().range("comment-score" + postId, start, end);
        // List<PostComment> result = new ArrayList<>();
        // HashOperations<String, Object, Object> hashOps = redisTemplate.opsForHash();
        // String hashKey = "comment" + postId;
        // for (Object field : tupleSet) {
        // String commentId = field.toString();
        // Object value = hashOps.get(hashKey, commentId);
        // result.add((PostComment) value);
        // }
        // return result;
    }

    @Override
    public List<PostReply> getReplyShardByScore(int parentCommentId, int start, int end) {
        if (!redisTemplate.hasKey("reply-score" + parentCommentId) || !redisTemplate.hasKey("reply" + parentCommentId)) {
            return null;
        }
        Set<Object> tupleSet = redisTemplate.opsForZSet().range("reply-score" + parentCommentId, start, end);
        return redisTemplate.opsForHash().multiGet("reply" + parentCommentId, tupleSet).stream()
                .map(item -> (PostReply) item)
                .collect(Collectors.toList());
    }

    @Override
    public List<PostComment> getCommentShardByTime(String key, int start, int end) {
        // List<PostComment> commentList;
        // commentList = caffeineCommentSet.getIfPresent(key);
        // if (commentList != null) {
        // return commentList;
        // }
        // Set<Object> tupleSet = redisTemplate.opsForZSet().range(key, start, end);
        // if (tupleSet != null) {
        // commentList = new ArrayList<>();
        // for (Object tuple : tupleSet) {
        // commentList.add((PostComment) tuple);
        // }
        // return commentList;
        // }
        // return commentList;
        return null;
    }

    @Override
    public void updateReplyCount(String key, UpdateReplyCount updateReplyCount) {
        // redisTemplate.opsForZSet().incrementScore(key, updateReplyCount.getParentCommentId(), updateReplyCount.getReplyCount());
        // List<PostComment> commentList = caffeineCommentSet.getIfPresent(key);
        // for (PostComment comment : commentList) {
        // if (comment.getPostCommentId() == updateReplyCount.getParentCommentId()) {
        // comment.setCommentLikeCount(comment.getCommentLikeCount() + updateReplyCount.getReplyCount());
        // break;
        // }
        // commentList.sort(Comparator.comparing(PostComment::getCommentLikeCount));
        // }
    }

    @Override
    public void deleteComment(String key) {
        redisTemplate.delete(key);
        // caffeineCommentSet.invalidate(key);
    }

    @Override
    public void deleteReply(String key) {
        redisTemplate.delete(key);
        // caffeineReplySet.invalidate(key);
    }

    @Override
    public void cancelLikeComment(CancelLikeComment cancelLikeComment) {
        PostComment postComment = (PostComment) redisTemplate.opsForHash().get("comment" + cancelLikeComment.getPostId(), cancelLikeComment.getCommentId());
        postComment.setCommentLikeCount(postComment.getCommentLikeCount() - 1);
        redisTemplate.opsForHash().put("comment" + cancelLikeComment.getPostId(), cancelLikeComment.getCommentId(), postComment);
        redisTemplate.opsForZSet().incrementScore("comment-score" + cancelLikeComment.getPostId(), cancelLikeComment.getCommentId(), -1);
        redisTemplate.opsForSet().remove("comment-like" + cancelLikeComment.getCommentId(), cancelLikeComment.getSenderId());
    }

    @Override
    public void cancelLikeReply(CancelLikeReply cancelLikeReply) {
        PostComment postComment = (PostComment) redisTemplate.opsForHash().get("reply" + cancelLikeReply.getParentCommentId(), cancelLikeReply.getCommentReplyId());
        postComment.setCommentLikeCount(postComment.getCommentLikeCount() - 1);
        redisTemplate.opsForHash().put("reply" + cancelLikeReply.getParentCommentId(), cancelLikeReply.getCommentReplyId(), postComment);
        redisTemplate.opsForZSet().incrementScore("reply-score" + cancelLikeReply.getParentCommentId(), cancelLikeReply.getCommentReplyId(), -1);
        redisTemplate.opsForSet().remove("reply-like" + cancelLikeReply.getParentCommentId(), cancelLikeReply.getSenderId());
    }
}
