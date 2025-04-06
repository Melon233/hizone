package com.example.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

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
import com.example.service.CacheService;
import com.github.benmanes.caffeine.cache.Cache;

@Service
public class CacheServiceImpl implements CacheService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private Cache<String, Object> caffeineCache;
    // @Autowired
    // private Cache<Integer, TreeSet<comment>> caffeineCommentSet;
    // @Autowired
    // private Cache<Integer, TreeSet<reply>> caffeineReplySet;

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
    public void addComment(Comment comment) {
        redisTemplate.opsForHash().put("comment" + comment.getPostId(),
                Integer.toString(comment.getCommentId()), comment);
        redisTemplate.opsForZSet().add("comment-score" + comment.getPostId(),
                comment.getCommentId(),
                comment.getCommentLikeCount());
    }

    @Override
    public void addReply(Reply reply) {
        redisTemplate.opsForHash().put("reply" + reply.getParentCommentId(),
                Integer.toString(reply.getReplyId()), reply);
        redisTemplate.opsForZSet().add("reply-score" + reply.getParentCommentId(),
                reply.getReplyId(),
                reply.getReplyLikeCount());
        Comment comment = (Comment) redisTemplate.opsForHash().get("comment" + reply.getPostId(), Integer.toString(reply.getParentCommentId()));
        comment.setReplyCount(comment.getReplyCount() + 1);
        redisTemplate.opsForHash().put("comment" + reply.getPostId(), Integer.toString(reply.getParentCommentId()), comment);
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
                !redisTemplate.hasKey("comment-score" + likeComment.getPostId())) {
            System.out.println("未命中！！！");
            return;
        }
        System.out.println("命中！！！");
        Comment comment = (Comment) redisTemplate.opsForHash().get("comment" + likeComment.getPostId(), Integer.toString(likeComment.getCommentId()));
        comment.setCommentLikeCount(comment.getCommentLikeCount() + 1);
        redisTemplate.opsForHash().put("comment" + likeComment.getPostId(), Integer.toString(likeComment.getCommentId()), comment);
        redisTemplate.opsForZSet().incrementScore("comment-score" + likeComment.getPostId(), likeComment.getCommentId(), 1);
        redisTemplate.opsForSet().add("comment-like" + likeComment.getPostId(), new CommentLike(likeComment.getPostId(), likeComment.getSenderId(), likeComment.getCommentId()));
    }

    @Override
    public void likeReply(LikeReply likeReply) {
        if (!redisTemplate.hasKey("reply" + likeReply.getParentCommentId()) ||
                !redisTemplate.hasKey("reply-score" + likeReply.getParentCommentId())) {
            return;
        }
        Reply reply = (Reply) redisTemplate.opsForHash().get("reply" + likeReply.getParentCommentId(), Integer.toString(likeReply.getReplyId()));
        reply.setReplyLikeCount(reply.getReplyLikeCount() + 1);
        redisTemplate.opsForHash().put("reply" + likeReply.getParentCommentId(), Integer.toString(likeReply.getReplyId()), reply);
        redisTemplate.opsForZSet().incrementScore("reply-score" + likeReply.getParentCommentId(), likeReply.getReplyId(), 1);
        redisTemplate.opsForSet().add("reply-like" + likeReply.getParentCommentId(), new ReplyLike(likeReply.getSenderId(), likeReply.getParentCommentId(), likeReply.getReplyId()));
    }

    @Override
    public void appendCommentShard(int postId, List<Comment> commentList, List<CommentLike> commentLikeList) {
        Map<String, Comment> commentMap = commentList.stream().collect(Collectors
                .toMap(comment -> Integer.toString(comment.getCommentId()),
                        comment -> comment));
        redisTemplate.opsForHash().putAll("comment" + postId, commentMap);
        Set<ZSetOperations.TypedTuple<Object>> tuples = commentList.stream()
                .map(comment -> new DefaultTypedTuple<Object>(comment.getCommentId(),
                        (double) comment.getCommentLikeCount()))
                .collect(Collectors.toSet());
        redisTemplate.opsForZSet().add("comment-score" + postId, tuples);
        if (commentLikeList.size() > 0) {
            redisTemplate.opsForSet().add("comment-like" + postId, commentLikeList.toArray());
        }
    }

    @Override
    public void appendReplyShard(int postId, List<Reply> replyList, List<ReplyLike> replyLikeList) {
        Map<String, Reply> replyMap = replyList.stream()
                .collect(Collectors.toMap(reply -> Integer.toString(reply.getReplyId()),
                        reply -> reply));
        redisTemplate.opsForHash().putAll("reply" + postId, replyMap);
        Set<ZSetOperations.TypedTuple<Object>> tuples = replyList.stream()
                .map(reply -> new DefaultTypedTuple<Object>(reply.getReplyId(),
                        (double) reply.getReplyLikeCount()))
                .collect(Collectors.toSet());
        redisTemplate.opsForZSet().add("reply-score" + postId, tuples);
        if (replyLikeList.size() > 0) {
            redisTemplate.opsForSet().add("reply-like" + postId, replyLikeList.toArray());
        }
    }

    @Override
    public List<CommentDetail> getCommentShardByScore(int postId, int userId, int start, int end) {
        if (!redisTemplate.hasKey("comment-score" + postId) || !redisTemplate.hasKey("comment" + postId)) {
            return new ArrayList<>();
        }
        List<Object> commentRankSet = redisTemplate.opsForZSet().reverseRange("comment-score" + postId, start, end)
                .stream()
                .map(item -> (Object) item.toString())
                .collect(Collectors.toList());
        System.out.println(commentRankSet);
        Set<CommentLike> commentLikeSet = redisTemplate.opsForSet().members("comment-like" + postId).stream()
                .map(item -> (CommentLike) item)
                .collect(Collectors.toSet());
        List<Comment> commentList = redisTemplate.opsForHash()
                .multiGet("comment" + postId, commentRankSet)
                .stream()
                .map(item -> (Comment) item)
                .collect(Collectors.toList());
        List<CommentDetail> commentDetailList = new ArrayList<>();
        for (Comment comment : commentList) {
            CommentDetail commentDetail = new CommentDetail();
            commentDetail.setCommentId(comment.getCommentId());
            commentDetail.setPostId(comment.getPostId());
            commentDetail.setSenderId(comment.getSenderId());
            commentDetail.setCommentContent(comment.getCommentContent());
            commentDetail.setCommentLikeCount(comment.getCommentLikeCount());
            commentDetail.setReplyCount(comment.getReplyCount());
            commentDetail.setCommentTime(comment.getCommentTime());
            commentDetail.setLiked(commentLikeSet.contains(new CommentLike(postId, userId, comment.getCommentId())));
            commentDetailList.add(commentDetail);
        }
        return commentDetailList;
    }

    @Override
    public List<ReplyDetail> getReplyShardByScore(int parentCommentId, int userId, int start, int end) {
        if (!redisTemplate.hasKey("reply-score" + parentCommentId) || !redisTemplate.hasKey("reply" + parentCommentId)) {
            return new ArrayList<>();
        }
        List<Object> replyRankSet = redisTemplate.opsForZSet().reverseRange("reply-score" + parentCommentId, start, end)
                .stream()
                .map(item -> (Object) item.toString())
                .collect(Collectors.toList());
        Set<ReplyLike> replyLikeSet = redisTemplate.opsForSet().members("reply-like" + parentCommentId).stream()
                .map(item -> (ReplyLike) item)
                .collect(Collectors.toSet());
        List<Reply> replyList = redisTemplate.opsForHash()
                .multiGet("reply" + parentCommentId, replyRankSet)
                .stream()
                .map(item -> (Reply) item)
                .collect(Collectors.toList());
        List<ReplyDetail> replyDetailList = new ArrayList<>();
        for (Reply reply : replyList) {
            ReplyDetail replyDetail = new ReplyDetail();
            replyDetail.setReplyId(reply.getReplyId());
            replyDetail.setParentCommentId(reply.getParentCommentId());
            replyDetail.setPostId(reply.getPostId());
            replyDetail.setSenderId(reply.getSenderId());
            replyDetail.setReplyContent(reply.getReplyContent());
            replyDetail.setReplyLikeCount(reply.getReplyLikeCount());
            replyDetail.setReplyTime(reply.getReplyTime());
            replyDetail.setLiked(replyLikeSet.contains(new ReplyLike(userId, parentCommentId, reply.getReplyId())));
            replyDetailList.add(replyDetail);
        }
        return replyDetailList;
    }

    @Override
    public List<Comment> getCommentShardByTime(String key, int start, int end) {
        // List<comment> commentList;
        // commentList = caffeineCommentSet.getIfPresent(key);
        // if (commentList != null) {
        // return commentList;
        // }
        // Set<Object> tupleSet = redisTemplate.opsForZSet().range(key, start, end);
        // if (tupleSet != null) {
        // commentList = new ArrayList<>();
        // for (Object tuple : tupleSet) {
        // commentList.add((comment) tuple);
        // }
        // return commentList;
        // }
        // return commentList;
        return null;
    }

    @Override
    public void updateReplyCount(String key, UpdateReplyCount updateReplyCount) {
        // redisTemplate.opsForZSet().incrementScore(key,
        // updateReplyCount.getParentCommentId(), updateReplyCount.getReplyCount());
        // List<comment> commentList = caffeineCommentSet.getIfPresent(key);
        // for (comment comment : commentList) {
        // if (comment.getcommentId() == updateReplyCount.getParentCommentId()) {
        // comment.setCommentLikeCount(comment.getCommentLikeCount() +
        // updateReplyCount.getReplyCount());
        // break;
        // }
        // commentList.sort(Comparator.comparing(comment::getCommentLikeCount));
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
        Comment comment = (Comment) redisTemplate.opsForHash().get("comment" + cancelLikeComment.getPostId(), Integer.toString(cancelLikeComment.getCommentId()));
        comment.setCommentLikeCount(comment.getCommentLikeCount() - 1);
        redisTemplate.opsForHash().put("comment" + cancelLikeComment.getPostId(), Integer.toString(cancelLikeComment.getCommentId()), comment);
        redisTemplate.opsForZSet().incrementScore("comment-score" + cancelLikeComment.getPostId(), cancelLikeComment.getCommentId(), -1);
        redisTemplate.opsForSet().remove("comment-like" + cancelLikeComment.getPostId(), new CommentLike(cancelLikeComment.getPostId(), cancelLikeComment.getSenderId(), cancelLikeComment.getCommentId()));
    }

    @Override
    public void cancelLikeReply(CancelLikeReply cancelLikeReply) {
        Reply reply = (Reply) redisTemplate.opsForHash().get("reply" + cancelLikeReply.getParentCommentId(), Integer.toString(cancelLikeReply.getReplyId()));
        reply.setReplyLikeCount(reply.getReplyLikeCount() - 1);
        redisTemplate.opsForHash().put("reply" + cancelLikeReply.getParentCommentId(), Integer.toString(cancelLikeReply.getReplyId()), reply);
        redisTemplate.opsForZSet().incrementScore("reply-score" + cancelLikeReply.getParentCommentId(), cancelLikeReply.getReplyId(), -1);
        redisTemplate.opsForSet().remove("reply-like" + cancelLikeReply.getParentCommentId(), new ReplyLike(cancelLikeReply.getSenderId(), cancelLikeReply.getParentCommentId(), cancelLikeReply.getReplyId()));
    }
}
