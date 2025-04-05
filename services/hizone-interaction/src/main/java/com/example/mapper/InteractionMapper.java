package com.example.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.example.hizone.dao.interaction.Interaction;
import com.example.hizone.dao.interaction.PostCollect;
import com.example.hizone.dao.interaction.PostLike;
import com.example.hizone.front.interaction.CancelCollectPost;
import com.example.hizone.front.interaction.CancelLikePost;
import com.example.hizone.front.interaction.CollectPost;
import com.example.hizone.front.interaction.LikePost;
import com.example.hizone.inter.UpdateCommentCount;
import com.example.hizone.inter.UserPost;

@Mapper
public interface InteractionMapper {

    @Insert("INSERT INTO post_like (post_id, sender_id) VALUES (#{postId}, #{senderId})")
    void insertPostLike(LikePost postLike);

    @Select("SELECT * FROM post_interaction WHERE post_id = #{postId}")
    Interaction selectInteractionById(int postId);

    @Insert("INSERT INTO post_collect (post_id, sender_id) VALUES (#{postId}, #{senderId})")
    void insertPostCollect(CollectPost collectPost);

    @Insert("INSERT INTO post_interaction (post_id) VALUES (#{postId})")
    void insertNewInteraction(int postId);

    @Select("SELECT * FROM post_like WHERE post_id = #{postId} AND sender_id = #{userId}")
    PostLike selectPostLike(UserPost userPost);

    @Select("SELECT * FROM post_collect WHERE post_id = #{postId} AND sender_id = #{userId}")
    PostCollect selectPostCollect(UserPost userPost);

    @Select("SELECT * FROM post_like WHERE post_id = #{postId}")
    List<LikePost> selectPostLikeList(int postId);

    @Select("SELECT * FROM post_collect WHERE post_id = #{postId}")
    List<CollectPost> selectPostCollectList(int postId);

    @Update("UPDATE post_interaction SET like_count = like_count + #{increment} WHERE post_id = #{postId}")
    void updateInteractionLikeCount(int postId, int increment);

    @Update("UPDATE post_interaction SET collect_count = collect_count + #{increment} WHERE post_id = #{postId}")
    void updateInteractionCollectCount(int postId, int increment);

    @Update("UPDATE post_interaction SET comment_count = comment_count + #{increment} WHERE post_id = #{postId}")
    void updateCommentCount(UpdateCommentCount updateCommentCount);

    @Delete("DELETE FROM post_like WHERE post_id = #{postId} AND sender_id = #{senderId}")
    void deletePostLike(CancelLikePost cancelLikePost);

    @Delete("DELETE FROM post_collect WHERE post_id = #{postId} AND sender_id = #{senderId}")
    void deletePostCollect(CancelCollectPost cancelCollectPost);
}
