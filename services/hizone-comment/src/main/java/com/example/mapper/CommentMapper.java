package com.example.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.example.hizone.request.comment.CancelLikeComment;
import com.example.hizone.request.comment.CancelLikeReply;
import com.example.hizone.request.comment.DeleteComment;
import com.example.hizone.request.comment.DeleteReply;
import com.example.hizone.request.comment.LikeComment;
import com.example.hizone.request.comment.LikeReply;
import com.example.hizone.request.comment.SendReply;
import com.example.hizone.request.comment.SendComment;
import com.example.hizone.table.comment.Comment;
import com.example.hizone.table.comment.CommentLike;
import com.example.hizone.table.comment.Reply;
import com.example.hizone.table.comment.ReplyLike;

@Mapper
public interface CommentMapper {

    @Insert("INSERT INTO comment (post_id, sender_id, comment_content, comment_time) VALUES (#{postId}, #{senderId}, #{commentContent}, #{commentTime})")
    @Options(useGeneratedKeys = true, keyProperty = "commentId", keyColumn = "comment_id")
    void insertComment(SendComment sendComment);

    @Insert("INSERT INTO reply (post_id, sender_id, reply_content, parent_comment_id, reply_time) VALUES (#{postId}, #{senderId}, #{replyContent}, #{parentCommentId},#{replyTime})")
    @Options(useGeneratedKeys = true, keyProperty = "replyId", keyColumn = "reply_id")
    void insertReplyComment(SendReply replyComment);

    @Select("SELECT * FROM comment WHERE post_id = #{postId}")
    List<Comment> selectCommentList(Long postId);

    @Select("SELECT * FROM reply WHERE parent_comment_id = #{commentId}")
    List<Reply> selectReplyList(Long commentId);

    @Insert("INSERT INTO comment_like (post_id,sender_id, comment_id) VALUES (#{postId}, #{senderId}, #{commentId})")
    void insertCommentLike(LikeComment likeComment);

    @Insert("INSERT INTO reply_like (parent_comment_id, sender_id, reply_id) VALUES (#{parentCommentId}, #{senderId}, #{replyId})")
    void insertCommentReplyLike(LikeReply likeReply);

    @Update("UPDATE comment SET comment_like_count = comment_like_count + 1 WHERE comment_id = #{commentId}")
    void updateCommentLikeCountIncrement(LikeComment likeComment);

    @Update("UPDATE comment SET comment_like_count = comment_like_count - 1 WHERE comment_id = #{commentId}")
    void updateCommentLikeCountDecrement(CancelLikeComment cancelLikeComment);

    @Update("UPDATE reply SET reply_like_count = reply_like_count + 1 WHERE reply_id = #{replyId}")
    void updateReplyLikeCountIncrement(LikeReply likeReply);

    @Update("UPDATE reply SET reply_like_count = reply_like_count - 1 WHERE reply_id = #{replyId}")
    void updateReplyLikeCountDecrement(CancelLikeReply cancelLikeReply);

    @Update("UPDATE comment SET reply_count = reply_count + 1 WHERE comment_id = #{parentCommentId}")
    void updateReplyCountIncrement(SendReply replyComment);

    @Update("UPDATE comment SET reply_count = reply_count - 1 WHERE comment_id = #{parentCommentId}")
    void updateReplyCountDecrement(DeleteReply deleteReply);

    @Delete("DELETE FROM comment WHERE comment_id = #{commentId}")
    void deleteComment(DeleteComment deleteComment);

    @Delete("DELETE FROM reply WHERE reply_id = #{replyId}")
    void deleteReply(DeleteReply deleteReply);

    @Delete("DELETE FROM comment_like WHERE comment_id = #{commentId} AND sender_id = #{senderId}")
    void cancelLikeComment(CancelLikeComment cancelLikeComment);

    @Delete("DELETE FROM reply_like WHERE reply_id = #{replyId} AND sender_id = #{senderId}")
    void cancelLikeReply(CancelLikeReply cancelLikeReply);

    @Select("SELECT * FROM comment_like WHERE post_id = #{postId}")
    List<CommentLike> selectCommentLikeList(Long postId);

    @Select("SELECT * FROM reply_like WHERE parent_comment_id = #{parentCommentId}")
    List<ReplyLike> selectReplyLikeList(Long parentCommentId);
}
