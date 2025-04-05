package com.example.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

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

@Mapper
public interface CommentMapper {

    @Insert("INSERT INTO post_comment (post_id, sender_id, comment_content, comment_time) VALUES (#{postId}, #{senderId}, #{commentContent}, #{commentTime})")
    @Options(useGeneratedKeys = true, keyProperty = "commentId", keyColumn = "post_comment_id")
    void insertComment(SendComment sendComment);

    @Insert("INSERT INTO comment_reply (post_id, sender_id, reply_content, parent_comment_id, reply_time) VALUES (#{postId}, #{senderId}, #{replyContent}, #{parentCommentId},#{replyTime})")
    @Options(useGeneratedKeys = true, keyProperty = "commentReplyId", keyColumn = "comment_reply_id")
    void insertReplyComment(ReplyComment replyComment);

    @Select("SELECT * FROM post_comment WHERE post_id = #{postId}")
    List<PostComment> selectCommentList(int postId);

    @Select("SELECT * FROM comment_reply WHERE parent_comment_id = #{commentId}")
    List<PostReply> selectReplyList(int commentId);

    @Insert("INSERT INTO comment_like (sender_id, comment_id) VALUES (#{senderId}, #{commentId})")
    void insertCommentLike(LikeComment likeComment);

    @Insert("INSERT INTO comment_reply_like (sender_id, comment_reply_id) VALUES (#{senderId}, #{commentReplyId})")
    void insertCommentReplyLike(LikeReply likeReply);

    @Update("UPDATE post_comment SET comment_like_count = comment_like_count + 1 WHERE comment_id = #{commentId}")
    void updateCommentLikeCount(LikeComment likeComment);

    @Update("UPDATE post_comment SET comment_reply_count = comment_reply_count + 1 WHERE comment_id = #{parentCommentId}")
    void updateCommentReplyCount(SendComment sendComment);

    @Delete("DELETE FROM post_comment WHERE comment_id = #{commentId} AND sender_id = #{senderId}")
    void deleteComment(DeleteComment deleteComment);

    @Delete("DELETE FROM comment_reply WHERE comment_reply_id = #{commentReplyId} AND sender_id = #{senderId}")
    void deleteReply(DeleteReply deleteReply);

    @Delete("DELETE FROM comment_like WHERE comment_id = #{commentId} AND sender_id = #{senderId}")
    void cancelLikeComment(CancelLikeComment cancelLikeComment);

    @Delete("DELETE FROM comment_reply_like WHERE comment_reply_id = #{commentReplyId} AND sender_id = #{senderId}")
    void cancelLikeReply(CancelLikeReply cancelLikeReply);
}
