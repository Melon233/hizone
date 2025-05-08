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

import com.example.feign.UserFeignClient;
import com.example.hizone.dto.UpdateReplyCount;
import com.example.hizone.request.comment.CancelLikeComment;
import com.example.hizone.request.comment.CancelLikeReply;
import com.example.hizone.request.comment.DeleteComment;
import com.example.hizone.request.comment.DeleteReply;
import com.example.hizone.request.comment.LikeComment;
import com.example.hizone.request.comment.LikeReply;
import com.example.hizone.response.CommentDetail;
import com.example.hizone.response.ReplyDetail;
import com.example.hizone.response.UserInfo;
import com.example.hizone.table.comment.Comment;
import com.example.hizone.table.comment.CommentLike;
import com.example.hizone.table.comment.Reply;
import com.example.hizone.table.comment.ReplyLike;
import com.example.service.CommentCacheService;
import com.github.benmanes.caffeine.cache.Cache;

@Service
public class CommentCacheServiceImpl implements CommentCacheService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private Cache<String, Object> caffeineCache;

    @Autowired
    private UserFeignClient userFeignClient;
    // @Autowired
    // private Cache<Long, TreeSet<comment>> caffeineCommentSet;
    // @Autowired
    // private Cache<Long, TreeSet<reply>> caffeineReplySet;

    @Override
    public void setCache(String key, Object value) {
        // caffeineCache.put(key, value);
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public Object getCache(String key) {
        Object cache;
        // cache = caffeineCache.getIfPresent(key);
        // if (cache != null) {
        //     return cache;
        // }
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
        redisTemplate.opsForHash().put("comment" + comment.getPostId(), Long.toString(comment.getCommentId()), comment);
        redisTemplate.opsForZSet().add("comment-score" + comment.getPostId(), comment.getCommentId(), comment.getLikeCount());
    }

    @Override
    public void addReply(Reply reply) {
        redisTemplate.opsForHash().put("reply" + reply.getParentCommentId(),
                Long.toString(reply.getReplyId()), reply);
        redisTemplate.opsForZSet().add("reply-score" + reply.getParentCommentId(),
                reply.getReplyId(),
                reply.getReplyLikeCount());
        Comment comment = (Comment) redisTemplate.opsForHash().get("comment" + reply.getPostId(), Long.toString(reply.getParentCommentId()));
        comment.setReplyCount(comment.getReplyCount() + 1);
        redisTemplate.opsForHash().put("comment" + reply.getPostId(), Long.toString(reply.getParentCommentId()), comment);
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
        Comment comment = (Comment) redisTemplate.opsForHash().get("comment" + likeComment.getPostId(), Long.toString(likeComment.getCommentId()));
        comment.setLikeCount(comment.getLikeCount() + 1);
        redisTemplate.opsForHash().put("comment" + likeComment.getPostId(), Long.toString(likeComment.getCommentId()), comment);
        redisTemplate.opsForZSet().incrementScore("comment-score" + likeComment.getPostId(), likeComment.getCommentId(), 1);
        redisTemplate.opsForSet().add("comment-like" + likeComment.getPostId(), new CommentLike(likeComment.getPostId(), likeComment.getSenderId(), likeComment.getCommentId()));
    }

    @Override
    public void likeReply(LikeReply likeReply) {
        if (!redisTemplate.hasKey("reply" + likeReply.getParentCommentId()) ||
                !redisTemplate.hasKey("reply-score" + likeReply.getParentCommentId())) {
            return;
        }
        Reply reply = (Reply) redisTemplate.opsForHash().get("reply" + likeReply.getParentCommentId(), Long.toString(likeReply.getReplyId()));
        reply.setReplyLikeCount(reply.getReplyLikeCount() + 1);
        redisTemplate.opsForHash().put("reply" + likeReply.getParentCommentId(), Long.toString(likeReply.getReplyId()), reply);
        redisTemplate.opsForZSet().incrementScore("reply-score" + likeReply.getParentCommentId(), likeReply.getReplyId(), 1);
        redisTemplate.opsForSet().add("reply-like" + likeReply.getParentCommentId(), new ReplyLike(likeReply.getSenderId(), likeReply.getParentCommentId(), likeReply.getReplyId()));
    }

    @Override
    public void appendCommentShard(Long postId, List<Comment> commentList, List<CommentLike> commentLikeList) {
        System.out.println(commentList);
        Map<String, Comment> commentMap = commentList.stream().collect(Collectors.toMap(comment -> Long.toString(comment.getCommentId()), comment -> comment));
        redisTemplate.opsForHash().putAll("comment" + postId, commentMap);
        Set<ZSetOperations.TypedTuple<Object>> tuples = commentList.stream()
                .map(comment -> new DefaultTypedTuple<Object>(comment.getCommentId(),
                        (double) comment.getLikeCount()))
                .collect(Collectors.toSet());
        redisTemplate.opsForZSet().add("comment-score" + postId, tuples);
        if (commentLikeList.size() > 0) {
            redisTemplate.opsForSet().add("comment-like" + postId, commentLikeList.toArray());
        }
    }

    @Override
    public void appendReplyShard(Long postId, List<Reply> replyList, List<ReplyLike> replyLikeList) {
        Map<String, Reply> replyMap = replyList.stream()
                .collect(Collectors.toMap(reply -> Long.toString(reply.getReplyId()),
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
    public List<CommentDetail> getCommentShardByScore(Long postId, Long userId, Long start, Long end) {
        if (!redisTemplate.hasKey("comment-score" + postId) || !redisTemplate.hasKey("comment" + postId)) {
            return new ArrayList<>();
        }
        List<Object> commentRankSet = redisTemplate.opsForZSet().reverseRange("comment-score" + postId, start, end)
                .stream()
                .map(item -> (Object) item.toString())
                .collect(Collectors.toList());
        Set<CommentLike> commentLikeSet = redisTemplate.opsForSet().members("comment-like" + postId).stream()
                .map(item -> (CommentLike) item)
                .collect(Collectors.toSet());
        List<Comment> commentList = redisTemplate.opsForHash()
                .multiGet("comment" + postId, commentRankSet)
                .stream()
                .map(item -> (Comment) item)
                .collect(Collectors.toList());
        List<UserInfo> userInfoList = userFeignClient.getUserInfoList(commentList.stream().mapToLong(Comment::getSenderId).boxed().toList());
        List<CommentDetail> commentDetailList = new ArrayList<>();
        for (Comment comment : commentList) {
            CommentDetail commentDetail = new CommentDetail();
            commentDetail.setCommentId(comment.getCommentId());
            commentDetail.setPostId(comment.getPostId());
            commentDetail.setSenderId(comment.getSenderId());
            commentDetail.setSenderName(userInfoList.get(commentList.indexOf(comment)).getNickname());
            commentDetail.setCommentContent(comment.getCommentContent());
            commentDetail.setLikeCount(comment.getLikeCount());
            commentDetail.setReplyCount(comment.getReplyCount());
            commentDetail.setCommentTime(comment.getCommentTime());
            commentDetail.setLiked(userId == -1 ? false : commentLikeSet.contains(new CommentLike(postId, userId, comment.getCommentId())));
            commentDetailList.add(commentDetail);
        }
        return commentDetailList;
    }

    @Override
    public List<ReplyDetail> getReplyShardByScore(Long parentCommentId, Long userId, Long start, Long end) {
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
        List<UserInfo> userInfoList = userFeignClient.getUserInfoList(replyList.stream().mapToLong(Reply::getSenderId).boxed().toList());
        for (Reply reply : replyList) {
            ReplyDetail replyDetail = new ReplyDetail();
            replyDetail.setReplyId(reply.getReplyId());
            replyDetail.setParentCommentId(reply.getParentCommentId());
            replyDetail.setPostId(reply.getPostId());
            replyDetail.setSenderId(reply.getSenderId());
            replyDetail.setSenderName(userInfoList.get(replyList.indexOf(reply)).getNickname());
            replyDetail.setReplyContent(reply.getReplyContent());
            replyDetail.setReplyLikeCount(reply.getReplyLikeCount());
            replyDetail.setReplyTime(reply.getReplyTime());
            replyDetail.setLiked(userId == -1 ? false : replyLikeSet.contains(new ReplyLike(userId, parentCommentId, reply.getReplyId())));
            replyDetailList.add(replyDetail);
        }
        return replyDetailList;
    }

    @Override
    public List<Comment> getCommentShardByTime(String key, Long start, Long end) {
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
        // comment.setLikeCount(comment.getLikeCount() +
        // updateReplyCount.getReplyCount());
        // break;
        // }
        // commentList.sort(Comparator.comparing(comment::getLikeCount));
        // }
    }

    @Override
    public void deleteComment(DeleteComment deleteComment) {
        redisTemplate.opsForHash().delete("comment" + deleteComment.getPostId(), Long.toString(deleteComment.getCommentId()));
        redisTemplate.opsForZSet().remove("comment-score" + deleteComment.getPostId(), deleteComment.getCommentId());
    }

    @Override
    public void deleteReply(DeleteReply deleteReply) {
        redisTemplate.opsForHash().delete("reply" + deleteReply.getParentCommentId(), Long.toString(deleteReply.getReplyId()));
        redisTemplate.opsForZSet().remove("reply-score" + deleteReply.getParentCommentId(), deleteReply.getReplyId());
        Comment comment = (Comment) redisTemplate.opsForHash().get("comment" + deleteReply.getPostId(), Long.toString(deleteReply.getParentCommentId()));
        comment.setReplyCount(comment.getReplyCount() - 1);
        redisTemplate.opsForHash().put("comment" + deleteReply.getPostId(), Long.toString(deleteReply.getParentCommentId()), comment);
    }

    @Override
    public void cancelLikeComment(CancelLikeComment cancelLikeComment) {
        Comment comment = (Comment) redisTemplate.opsForHash().get("comment" + cancelLikeComment.getPostId(), Long.toString(cancelLikeComment.getCommentId()));
        comment.setLikeCount(comment.getLikeCount() - 1);
        redisTemplate.opsForHash().put("comment" + cancelLikeComment.getPostId(), Long.toString(cancelLikeComment.getCommentId()), comment);
        redisTemplate.opsForZSet().incrementScore("comment-score" + cancelLikeComment.getPostId(), cancelLikeComment.getCommentId(), -1);
        redisTemplate.opsForSet().remove("comment-like" + cancelLikeComment.getPostId(), new CommentLike(cancelLikeComment.getPostId(), cancelLikeComment.getSenderId(), cancelLikeComment.getCommentId()));
    }

    @Override
    public void cancelLikeReply(CancelLikeReply cancelLikeReply) {
        Reply reply = (Reply) redisTemplate.opsForHash().get("reply" + cancelLikeReply.getParentCommentId(), Long.toString(cancelLikeReply.getReplyId()));
        reply.setReplyLikeCount(reply.getReplyLikeCount() - 1);
        redisTemplate.opsForHash().put("reply" + cancelLikeReply.getParentCommentId(), Long.toString(cancelLikeReply.getReplyId()), reply);
        redisTemplate.opsForZSet().incrementScore("reply-score" + cancelLikeReply.getParentCommentId(), cancelLikeReply.getReplyId(), -1);
        redisTemplate.opsForSet().remove("reply-like" + cancelLikeReply.getParentCommentId(), new ReplyLike(cancelLikeReply.getSenderId(), cancelLikeReply.getParentCommentId(), cancelLikeReply.getReplyId()));
    }
}
