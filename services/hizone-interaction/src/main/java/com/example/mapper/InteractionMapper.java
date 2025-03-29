package com.example.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.example.fenta.dao.interaction.Interaction;
import com.example.fenta.dao.interaction.PostCollect;
import com.example.fenta.dao.interaction.PostLike;
import com.example.fenta.front.interaction.CollectPost;
import com.example.fenta.front.interaction.LikePost;
import com.example.fenta.inter.UserPost;

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
}
