package com.example.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.example.hizone.request.user.UpdateUserInfo;
import com.example.hizone.table.user.User;
import com.example.hizone.table.user.UserMetadata;

@Mapper
public interface UserMapper {

    @Update("UPDATE user SET nickname = #{nickname} WHERE user_id = #{userId}")
    void updateUserInfo(UpdateUserInfo updateUserInfo);

    @Update("UPDATE user_metadata SET fan_count = fan_count + #{increment} WHERE user_id = #{userId}")
    void updateFanCount(Long userId, Integer increment);

    @Update("UPDATE user_metadata SET follow_count = follow_count + #{increment} WHERE user_id = #{userId}")
    void updateFollowCount(Long userId, Integer increment);

    @Update("UPDATE user_metadata SET collect_count = collect_count + #{increment} WHERE user_id = #{userId}")
    void updateCollectCount(Long userId, Integer increment);

    @Update("UPDATE user_metadata SET post_count = post_count + #{increment} WHERE user_id = #{userId}")
    void updatePostCount(Long userId, Integer increment);

    @Update("UPDATE user_metadata SET liked_count = liked_count + #{increment} WHERE user_id = #{userId}")
    void updateLikedCount(Long userId, Integer increment);

    @Select("SELECT * FROM user WHERE user_id = #{userId}")
    User selectUser(Long userId);

    @Select("SELECT * FROM user")
    List<User> getAllUserList();

    @Select("SELECT * FROM user_metadata WHERE user_id = #{userId}")
    UserMetadata selectUserMetadata(Long userId);
}
