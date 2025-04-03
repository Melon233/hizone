package com.example.hizone.utility;

import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;

import com.example.hizone.dao.comment.PostComment;
import com.example.hizone.dao.comment.PostReply;
import com.example.hizone.front.interaction.CollectPost;
import com.example.hizone.front.interaction.LikePost;

public class RedisUtility {

    public static Set<TypedTuple<Object>> convertCommentListToTypedTupleListByScore(List<PostComment> commentList) {
        Set<TypedTuple<Object>> tupleSet = new HashSet<>();
        for (PostComment comment : commentList) {
            // 创建 TypedTuple，value 是 Comment 对象，score 是时间戳
            TypedTuple<Object> tuple = new ZSetOperations.TypedTuple<Object>() {

                @Override
                public Object getValue() {
                    return comment; // 存整个 Comment 对象
                }

                @Override
                public Double getScore() {
                    return (double) comment.getCommentLikeCount(); // 时间戳作为分数
                }

                // 实现 compareTo 方法（可选，通常由 DefaultTypedTuple 处理）
                @Override
                public int compareTo(TypedTuple<Object> o) {
                    return this.getScore().compareTo(o.getScore());
                }
            };
            tupleSet.add(tuple);
        }
        return tupleSet;
    }
    // public static Set<TypedTuple<Object>> convertCommentListToTypedTupleListByTime(List<PostComment> commentList) {
    // Set<TypedTuple<Object>> tupleSet = new HashSet<>();
    // for (PostComment comment : commentList) {
    // // 创建 TypedTuple，value 是 Comment 对象，score 是时间戳
    // TypedTuple<Object> tuple = new ZSetOperations.TypedTuple<Object>() {
    // @Override
    // public Object getValue() {
    // return comment; // 存整个 Comment 对象
    // }
    // @Override
    // public Double getScore() {
    // return (double) comment.getCommentTime().toEpochSecond(ZoneOffset.of("+8")); // 时间戳作为分数
    // }
    // // 实现 compareTo 方法（可选，通常由 DefaultTypedTuple 处理）
    // @Override
    // public int compareTo(TypedTuple<Object> o) {
    // return this.getScore().compareTo(o.getScore());
    // }
    // };
    // tupleSet.add(tuple);
    // }
    // return tupleSet;
    // }

    public static Set<TypedTuple<Object>> convertReplyListToTypedTupleListByScore(List<PostReply> replyList) {
        Set<TypedTuple<Object>> tupleSet = new HashSet<>();
        for (PostReply comment : replyList) {
            // 创建 TypedTuple，value 是 Comment 对象，score 是时间戳
            TypedTuple<Object> tuple = new ZSetOperations.TypedTuple<Object>() {

                @Override
                public Object getValue() {
                    return comment; // 存整个 Comment 对象
                }

                @Override
                public Double getScore() {
                    return (double) comment.getReplyTime().toEpochSecond(ZoneOffset.of("+8")); // 时间戳作为分数
                }

                // 实现 compareTo 方法（可选，通常由 DefaultTypedTuple 处理）
                @Override
                public int compareTo(TypedTuple<Object> o) {
                    return this.getScore().compareTo(o.getScore());
                }
            };
            tupleSet.add(tuple);
        }
        return tupleSet;
    }

    public static Set<TypedTuple<Object>> convertLikePostListToTypedTupleListByScore(List<LikePost> likePostList) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'convertLikePostListToTypedTupleListByScore'");
    }

    public static Set<TypedTuple<Object>> convertCollectPostListToTypedTupleListByScore(List<CollectPost> collectPostList) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'convertCollectPostListToTypedTupleListByScore'");
    }
}
