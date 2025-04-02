package com.example.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.example.hizone.dao.user.User;
import com.example.hizone.dao.user.UserMetadata;
import com.example.hizone.front.user.UpdateUserInfo;
import com.example.hizone.inter.UpdateUserMetadata;
import com.example.hizone.outer.UserDetail;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM user WHERE user_id = #{userId}")
    User getUser(int userId);

    @Select("SELECT * FROM user_metadata WHERE user_id = #{userId}")
    UserMetadata getUserMetadata(int userId);

    @Select("SELECT user.*, user_metadata.* FROM user JOIN  user_metadata ON user.user_id = user_metadata.user_id WHERE user.user_id = #{userId}")
    UserDetail getUserDetail(int userId);

    @Select("SELECT * FROM user")
    List<User> getAllUserList();

    @Update("UPDATE user SET nickname = #{nickname} WHERE user_id = #{userId}")
    void updateUserInfo(UpdateUserInfo updateUserInfo);

    @Update("UPDATE user_metadata SET fan_count = fan_count + #{fanCount}, follow_count = follow_count + #{followCount}, collect_count = collect_count + #{collectCount}, post_count = post_count + #{postCount}, liked_count = liked_count + #{likedCount} WHERE user_id = #{userId}")
    void updateUserMetadata(UpdateUserMetadata updateUserMetadata);
}
