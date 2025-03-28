package com.example.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.example.fenta.dao.follow.Follow;
import com.example.fenta.front.follow.AddFollow;
import com.example.fenta.front.follow.DeleteFan;
import com.example.fenta.front.follow.DeleteFollow;

@Mapper
public interface FollowMapper {

    @Select("SELECT * FROM follow WHERE followed_id = #{userId}")
    List<Follow> selectFanList(int userId);

    @Select("SELECT * FROM follow WHERE follower_id = #{userId}")
    List<Follow> selectFollowList(int userId);

    @Delete("DELETE FROM follow WHERE follower_id = #{followerId} AND followed_id = #{followedId}")
    void deleteFollow(DeleteFollow deleteFollow);

    @Insert("INSERT INTO follow(follower_id, followed_id) VALUES(#{followerId}, #{followedId})")
    void insertFollow(AddFollow addFollow);

    @Delete("DELETE FROM follow WHERE follower_id = #{followerId} AND followed_id = #{followedId}")
    void deleteFan(DeleteFan deleteFan);
}
