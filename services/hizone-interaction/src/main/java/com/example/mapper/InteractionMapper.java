package com.example.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.example.fenta.dao.interaction.Interaction;
import com.example.fenta.front.interaction.CollectPost;
import com.example.fenta.front.interaction.LikePost;

@Mapper
public interface InteractionMapper {

    @Insert("INSERT INTO post_like (post_id, sender_id) VALUES (#{postId}, #{senderId})")
    void insertPostLike(LikePost postLike);

    @Select("SELECT * FROM interaction WHERE post_id = #{postId}")
    Interaction selectInteractionById(int postId);

    @Insert("INSERT INTO post_collect (post_id, sender_id) VALUES (#{postId}, #{senderId})")
    void insertPostCollect(CollectPost collectPost);
}
