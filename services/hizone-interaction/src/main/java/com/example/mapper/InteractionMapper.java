package com.example.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.example.hizone.dto.UpdateCommentCount;
import com.example.hizone.dto.UserPost;
import com.example.hizone.request.interaction.CancelCollectPost;
import com.example.hizone.request.interaction.CancelLikePost;
import com.example.hizone.request.interaction.CollectPost;
import com.example.hizone.request.interaction.LikePost;
import com.example.hizone.table.interaction.Interaction;
import com.example.hizone.table.interaction.PostCollect;
import com.example.hizone.table.interaction.PostLike;

@Mapper
public interface InteractionMapper {

    @Insert("INSERT INTO post_like (post_id, sender_id) VALUES (#{postId}, #{senderId})")
    void insertPostLike(LikePost postLike);

    @Select("SELECT * FROM post_interaction WHERE post_id = #{postId}")
    Interaction selectInteractionById(Long postId);

    @Insert("INSERT INTO post_collect (post_id, sender_id) VALUES (#{postId}, #{senderId})")
    void insertPostCollect(CollectPost collectPost);

    @Insert("INSERT INTO post_interaction (post_id) VALUES (#{postId})")
    void insertNewInteraction(Long postId);

    @Select("SELECT * FROM post_like WHERE post_id = #{postId} AND sender_id = #{userId}")
    PostLike selectPostLike(UserPost userPost);

    @Select("SELECT * FROM post_collect WHERE post_id = #{postId} AND sender_id = #{userId}")
    PostCollect selectPostCollect(UserPost userPost);

    @Select("SELECT post_id,sender_id  FROM post_like WHERE post_id = #{postId}")
    List<LikePost> selectPostLikeList(Long postId);

    @Select("SELECT post_id,sender_id FROM post_collect WHERE post_id = #{postId}")
    List<CollectPost> selectPostCollectList(Long postId);

    @Update("UPDATE post_interaction SET like_count = like_count + #{increment} WHERE post_id = #{postId}")
    void updateInteractionLikeCount(Long postId, Long increment);

    @Update("UPDATE post_interaction SET collect_count = collect_count + #{increment} WHERE post_id = #{postId}")
    void updateInteractionCollectCount(Long postId, Long increment);

    @Update("UPDATE post_interaction SET comment_count = comment_count + #{increment} WHERE post_id = #{postId}")
    void updateCommentCount(UpdateCommentCount updateCommentCount);

    @Delete("DELETE FROM post_like WHERE post_id = #{postId} AND sender_id = #{senderId}")
    void deletePostLike(CancelLikePost cancelLikePost);

    @Delete("DELETE FROM post_collect WHERE post_id = #{postId} AND sender_id = #{senderId}")
    void deletePostCollect(CancelCollectPost cancelCollectPost);
}
