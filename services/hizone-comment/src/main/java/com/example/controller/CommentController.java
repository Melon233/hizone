package com.example.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.feign.InteractionFeignClient;
import com.example.fenta.dao.comment.PostComment;
import com.example.fenta.dao.comment.PostReply;
import com.example.fenta.front.comment.CancelLikeComment;
import com.example.fenta.front.comment.CancelLikeReply;
import com.example.fenta.front.comment.DeleteComment;
import com.example.fenta.front.comment.DeleteReply;
import com.example.fenta.front.comment.LikeComment;
import com.example.fenta.front.comment.LikeReply;
import com.example.fenta.front.comment.ReplyComment;
import com.example.fenta.front.comment.SendComment;
import com.example.fenta.inter.UpdateCommentCount;
import com.example.service.CacheService;
import com.example.service.CommentService;

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
    public List<PostComment> getCommentList(@RequestParam("post_id") int postId) {
        List<PostComment> commentList = cacheService.getCommentShardByScore("comment" + postId, 0, 10);
        if (commentList != null) {
            return commentList;
        }
        commentList = commentService.getCommentList(postId);
        cacheService.appendCommentShard("comment" + postId, commentList);
        return commentList;
    }

    @GetMapping("/getReplyList")
    public List<PostReply> getReplyCommentList(@RequestParam("parent_comment_id") int parentCommentId) {
        List<PostReply> replyList = cacheService.getReplyShardByScore("reply" + parentCommentId, 0, 10);
        if (replyList != null) {
            return replyList;
        }
        replyList = commentService.getReplyList(parentCommentId);
        cacheService.appendReplyShard("reply" + parentCommentId, replyList);
        return replyList;
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
    public String sendComment(@RequestBody SendComment sendComment) {
        // 设置时间
        sendComment.setCommentTime(LocalDateTime.now());
        // 添加评论到数据库
        commentService.addComment(sendComment);
        // 更新交互元数据缓存
        UpdateCommentCount updateCommentCount = new UpdateCommentCount();
        updateCommentCount.setPostId(sendComment.getPostId());
        updateCommentCount.setCommentCount(1);
        interactionFeignClient.updateCommentCount(updateCommentCount);
        // 添加评论到缓存
        PostComment comment = new PostComment();
        comment.setCommentContent(sendComment.getCommentContent());
        comment.setCommentId(sendComment.getCommentId());
        comment.setCommentTime(sendComment.getCommentTime());
        comment.setPostId(sendComment.getPostId());
        comment.setSenderId(sendComment.getSenderId());
        cacheService.addComment("comment" + sendComment.getPostId(), comment);
        return "success";
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
        cacheService.likeComment("like-comment" + likeComment.getCommentId(), likeComment);
        return "success";
    }

    /**
     * 回复评论
     * 高频高精数据-使用缓存并且数据更新时总是更新缓存
     * 
     * @param replyComment
     * @return
     */
    @PostMapping("/sendReply")
    public String sendReply(@RequestBody ReplyComment sendReply) {
        // 设置时间
        sendReply.setReplyTime(LocalDateTime.now());
        // 添加评论到数据库
        commentService.addReply(sendReply);
        // 更新交互元数据缓存
        UpdateCommentCount updateCommentCount = new UpdateCommentCount();
        updateCommentCount.setPostId(sendReply.getPostId());
        updateCommentCount.setCommentCount(1);
        interactionFeignClient.updateCommentCount(updateCommentCount);
        // 添加评论到缓存
        PostReply comment = new PostReply();
        comment.setReplyContent(sendReply.getReplyContent());
        comment.setCommentReplyId(sendReply.getCommentReplyId());
        comment.setReplyTime(sendReply.getReplyTime());
        comment.setPostId(sendReply.getPostId());
        comment.setSenderId(sendReply.getSenderId());
        cacheService.addReply("reply" + sendReply.getParentCommentId(), comment);
        // 更新评论回复数缓存
        cacheService.updateReplyCount("", null);
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
        cacheService.likeReply("like-reply" + likeReply.getCommentReplyId(), likeReply);
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
        cacheService.deleteComment("comment" + deleteComment.getCommentId());
        return "success";
    }

    @PostMapping("/deleteReply")
    public String deleteReply(@RequestBody DeleteReply deleteReply) {
        commentService.deleteReply(deleteReply);
        cacheService.deleteReply("reply" + deleteReply.getCommentReplyId());
        return "success";
    }

    @PostMapping("/cancelLikeComment")
    public String cancelLikeComment(@RequestBody CancelLikeComment cancelLikeComment) {
        commentService.cancelLikeComment(cancelLikeComment);
        cacheService.cancelLikeComment("like-comment" + cancelLikeComment.getCommentId(), cancelLikeComment.getCommentId());
        return "success";
    }

    @PostMapping("/cancelLikeReply")
    public String cancelLikeReply(@RequestBody CancelLikeReply cancelLikeReply) {
        commentService.cancelLikeReply(cancelLikeReply);
        cacheService.cancelLikeReply("like-reply" + cancelLikeReply.getCommentReplyId(), cancelLikeReply.getCommentReplyId());
        return "success";
    }
}
