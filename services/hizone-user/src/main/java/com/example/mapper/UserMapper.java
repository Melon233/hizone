package com.example.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.example.hizone.dto.UpdateUserMetadata;
import com.example.hizone.request.user.UpdateUserInfo;
import com.example.hizone.response.UserDetail;
import com.example.hizone.table.user.User;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM user WHERE user_id = #{userId}")
    User getUser(Long userId);

    @Select("SELECT * FROM user WHERE user_id = #{userId}")
    UserDetail getUserDetail(Long userId);

    @Select("SELECT * FROM user")
    List<User> getAllUserList();

    @Update("UPDATE user SET nickname = #{nickname} WHERE user_id = #{userId}")
    void updateUserInfo(UpdateUserInfo updateUserInfo);

    @Update("UPDATE user_metadata SET fan_count = fan_count + #{fanCount}, follow_count = follow_count + #{followCount}, collect_count = collect_count + #{collectCount}, post_count = post_count + #{postCount}, liked_count = liked_count + #{likedCount} WHERE user_id = #{userId}")
    void updateUserMetadata(UpdateUserMetadata updateUserMetadata);
}
