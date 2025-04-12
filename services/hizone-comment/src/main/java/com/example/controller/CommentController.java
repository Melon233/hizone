package com.example.controller;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.apache.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.feign.InteractionFeignClient;
import com.example.hizone.dto.UpdateCommentCount;
import com.example.hizone.request.comment.CancelLikeComment;
import com.example.hizone.request.comment.CancelLikeReply;
import com.example.hizone.request.comment.DeleteComment;
import com.example.hizone.request.comment.DeleteReply;
import com.example.hizone.request.comment.LikeComment;
import com.example.hizone.request.comment.LikeReply;
import com.example.hizone.request.comment.SendReply;
import com.example.hizone.request.comment.SendComment;
import com.example.hizone.response.CommentDetail;
import com.example.hizone.response.ReplyDetail;
import com.example.hizone.table.comment.Comment;
import com.example.hizone.table.comment.CommentLike;
import com.example.hizone.table.comment.Reply;
import com.example.hizone.table.comment.ReplyLike;
import com.example.hizone.utility.TokenUtility;
import com.example.service.CacheService;
import com.example.service.CommentService;

@RequestMapping("/comment")
@GlobalTransactional
@CrossOrigin
@RestController
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private InteractionFeignClient interactionFeignClient;

    /**
     * 获取帖子评论列表
     * 
     * @param postId
     * @return
     */
    @GetMapping("/getCommentList")
    public List<CommentDetail> getCommentList(@RequestHeader(value = "Token", required = false) String token, @RequestParam("post_id") int postId) {
        int userId = token == null ? -1 : TokenUtility.extractUserId(token);
        List<CommentDetail> commentDetailList = cacheService.getCommentShardByScore(postId, userId, 0, 10);
        if (!commentDetailList.isEmpty()) {
            return commentDetailList;
        }
        List<Comment> commentList = commentService.getCommentList(postId);
        List<CommentLike> commentLikeList = commentService.getCommentLikeList(postId);
        if (!commentList.isEmpty()) {
            cacheService.appendCommentShard(postId, commentList, commentLikeList);
            return cacheService.getCommentShardByScore(postId, userId, 0, 10);
        } else {
            return new ArrayList<>();
        }
    }

    @GetMapping("/getReplyList")
    public List<ReplyDetail> getReplyCommentList(@RequestHeader(value = "Token", required = false) String token, @RequestParam("parent_comment_id") int parentCommentId) {
        int userId = token == null ? -1 : TokenUtility.extractUserId(token);
        List<ReplyDetail> replyDetailList = cacheService.getReplyShardByScore(parentCommentId, userId, 0, 10);
        if (!replyDetailList.isEmpty()) {
            return replyDetailList;
        }
        List<Reply> replyList = commentService.getReplyList(parentCommentId);
        List<ReplyLike> replyLikeList = commentService.getReplyLikeList(parentCommentId);
        if (!replyList.isEmpty()) {
            cacheService.appendReplyShard(parentCommentId, replyList, replyLikeList);
            return cacheService.getReplyShardByScore(parentCommentId, userId, 0, 10);
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * 发送评论
     * 高频高精数据-使用缓存并且数据更新时总是更新缓存
     * 包括评论回复数也是高频高精数据
     * 评论记录异步到数据库
     * 检查是否有缓存，没有则从数据库抓取帖子评论
     * 更新评论记录缓存和帖子交互数据元数据缓存
     * 
     * @param sendComment
     * @return
     */
    @PostMapping("/sendComment")
    public Comment sendComment(@RequestBody SendComment sendComment) {
        // 设置时间
        sendComment.setCommentTime(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString());
        // 添加评论到数据库
        commentService.addComment(sendComment);
        // 更新交互元数据缓存
        UpdateCommentCount updateCommentCount = new UpdateCommentCount();
        updateCommentCount.setPostId(sendComment.getPostId());
        updateCommentCount.setIncrement(1L);
        interactionFeignClient.updateCommentCount(updateCommentCount);
        // 添加评论到缓存
        Comment comment = new Comment();
        comment.setCommentContent(sendComment.getCommentContent());
        comment.setCommentId(sendComment.getCommentId());
        comment.setCommentTime(sendComment.getCommentTime());
        comment.setPostId(sendComment.getPostId());
        comment.setSenderId(sendComment.getSenderId());
        cacheService.addComment(comment);
        return comment;
    }

    /**
     * 回复评论
     * 高频高精数据-使用缓存并且数据更新时总是更新缓存
     * 
     * @param replyComment
     * @return
     */
    @PostMapping("/sendReply")
    public Reply sendReply(@RequestBody SendReply sendReply) {
        // 设置时间
        sendReply.setReplyTime(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString());
        // 添加评论到数据库
        commentService.addReply(sendReply);
        // 更新交互元数据缓存
        UpdateCommentCount updateCommentCount = new UpdateCommentCount();
        updateCommentCount.setPostId(sendReply.getPostId());
        updateCommentCount.setIncrement(1L);
        interactionFeignClient.updateCommentCount(updateCommentCount);
        // 添加评论到缓存
        Reply reply = new Reply();
        reply.setReplyContent(sendReply.getReplyContent());
        reply.setReplyId(sendReply.getReplyId());
        reply.setReplyTime(sendReply.getReplyTime());
        reply.setPostId(sendReply.getPostId());
        reply.setSenderId(sendReply.getSenderId());
        reply.setParentCommentId(sendReply.getParentCommentId());
        cacheService.addReply(reply);
        return reply;
    }

    /**
     * 点赞评论
     * 高频高精数据-记录异步到数据库修改缓存元数据
     * 
     * @param likeComment
     * @return
     */
    @PostMapping("/likeComment")
    public String likeComment(@RequestBody LikeComment likeComment) {
        commentService.addLikeComment(likeComment);
        cacheService.likeComment(likeComment);
        return "success";
    }

    /**
     * 点赞回复
     * 高频低精数据-记录异步到数据库修改缓存元数据
     * 
     * @param likeReply
     * @return
     */
    @PostMapping("/likeReply")
    public String likeReply(@RequestBody LikeReply likeReply) {
        commentService.addLikeReply(likeReply);
        cacheService.likeReply(likeReply);
        return "success";
    }

    @PostMapping("/cancelLikeComment")
    public String cancelLikeComment(@RequestBody CancelLikeComment cancelLikeComment) {
        commentService.cancelLikeComment(cancelLikeComment);
        cacheService.cancelLikeComment(cancelLikeComment);
        return "success";
    }

    @PostMapping("/cancelLikeReply")
    public String cancelLikeReply(@RequestBody CancelLikeReply cancelLikeReply) {
        commentService.cancelLikeReply(cancelLikeReply);
        cacheService.cancelLikeReply(cancelLikeReply);
        return "success";
    }

    /**
     * 删除评论
     * 高频低精数据-记录异步到数据库修改缓存记录
     * 
     * @param deleteComment
     * @return
     */
    @PostMapping("/deleteComment")
    public String deleteComment(@RequestBody DeleteComment deleteComment) {
        commentService.deleteComment(deleteComment);
        // 更新交互元数据缓存
        UpdateCommentCount updateCommentCount = new UpdateCommentCount();
        updateCommentCount.setPostId(deleteComment.getPostId());
        updateCommentCount.setIncrement(-1L);
        interactionFeignClient.updateCommentCount(updateCommentCount);
        cacheService.deleteComment(deleteComment);
        return "success";
    }

    @PostMapping("/deleteReply")
    public String deleteReply(@RequestBody DeleteReply deleteReply) {
        commentService.deleteReply(deleteReply);
        // 更新交互元数据缓存
        UpdateCommentCount updateCommentCount = new UpdateCommentCount();
        updateCommentCount.setPostId(deleteReply.getPostId());
        updateCommentCount.setIncrement(-1L);
        interactionFeignClient.updateCommentCount(updateCommentCount);
        cacheService.deleteReply(deleteReply);
        return "success";
    }
}
