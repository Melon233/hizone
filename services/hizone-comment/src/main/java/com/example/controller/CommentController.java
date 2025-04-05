package com.example.controller;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.feign.InteractionFeignClient;
import com.example.hizone.dao.comment.PostComment;
import com.example.hizone.dao.comment.PostReply;
import com.example.hizone.front.comment.CancelLikeComment;
import com.example.hizone.front.comment.CancelLikeReply;
import com.example.hizone.front.comment.DeleteComment;
import com.example.hizone.front.comment.DeleteReply;
import com.example.hizone.front.comment.LikeComment;
import com.example.hizone.front.comment.LikeReply;
import com.example.hizone.front.comment.ReplyComment;
import com.example.hizone.front.comment.SendComment;
import com.example.hizone.inter.UpdateCommentCount;
import com.example.service.CacheService;
import com.example.service.CommentService;

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
    public List<PostComment> getCommentList(@RequestParam("post_id") int postId) {
        List<PostComment> commentList = cacheService.getCommentShardByScore(postId, 0, 10);
        if (commentList != null) {
            return commentList;
        }
        commentList = commentService.getCommentList(postId);
        if (commentList != null) {
            cacheService.appendCommentShard(postId, commentList);
        }
        return commentList;
    }

    @GetMapping("/getReplyList")
    public List<PostReply> getReplyCommentList(@RequestParam("parent_comment_id") int parentCommentId) {
        List<PostReply> replyList = cacheService.getReplyShardByScore(parentCommentId, 0, 10);
        if (replyList != null) {
            return replyList;
        }
        replyList = commentService.getReplyList(parentCommentId);
        cacheService.appendReplyShard(parentCommentId, replyList);
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
    public PostComment sendComment(@RequestBody SendComment sendComment) {
        System.out.println("sendComment" + sendComment.toString());
        // 设置时间
        sendComment.setCommentTime(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString());
        System.out.println(sendComment.getCommentTime());
        // 添加评论到数据库
        commentService.addComment(sendComment);
        // 更新交互元数据缓存
        UpdateCommentCount updateCommentCount = new UpdateCommentCount();
        updateCommentCount.setPostId(sendComment.getPostId());
        updateCommentCount.setIncrement(1);
        interactionFeignClient.updateCommentCount(updateCommentCount);
        // 添加评论到缓存
        PostComment comment = new PostComment();
        comment.setCommentContent(sendComment.getCommentContent());
        comment.setPostCommentId(sendComment.getCommentId());
        comment.setCommentTime(sendComment.getCommentTime());
        comment.setPostId(sendComment.getPostId());
        comment.setSenderId(sendComment.getSenderId());
        cacheService.addComment(comment);
        return comment;
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
     * 回复评论
     * 高频高精数据-使用缓存并且数据更新时总是更新缓存
     * 
     * @param replyComment
     * @return
     */
    @PostMapping("/sendReply")
    public PostReply sendReply(@RequestBody ReplyComment sendReply) {
        // 设置时间
        sendReply.setReplyTime(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString());
        // 添加评论到数据库
        commentService.addReply(sendReply);
        // 更新交互元数据缓存
        UpdateCommentCount updateCommentCount = new UpdateCommentCount();
        updateCommentCount.setPostId(sendReply.getPostId());
        updateCommentCount.setIncrement(1);
        interactionFeignClient.updateCommentCount(updateCommentCount);
        // 添加评论到缓存
        PostReply comment = new PostReply();
        comment.setReplyContent(sendReply.getReplyContent());
        comment.setCommentReplyId(sendReply.getCommentReplyId());
        comment.setReplyTime(sendReply.getReplyTime());
        comment.setPostId(sendReply.getPostId());
        comment.setSenderId(sendReply.getSenderId());
        comment.setParentCommentId(sendReply.getParentCommentId());
        cacheService.addReply(comment);
        // 更新评论回复数缓存
        cacheService.updateReplyCount("", null);
        return comment;
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
}
