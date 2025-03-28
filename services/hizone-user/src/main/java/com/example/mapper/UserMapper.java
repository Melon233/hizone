package com.example.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.example.fenta.dao.user.User;
import com.example.fenta.dao.user.UserDetail;
import com.example.fenta.dao.user.UserMetadata;
import com.example.fenta.front.user.UpdateUser;
import com.example.fenta.inter.UpdateUserMetadata;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM user WHERE user_id = #{userId}")
    User getUser(int userId);

    @Select("SELECT * FROM user_metadata WHERE user_id = #{userId}")
    UserMetadata getUserMetadata(int userId);

    @Select("SELECT user.*, user_metadata.* FROM user JOIN  user_metadata ON user.user_id = user_metadata.user_id WHERE user_id = #{userId}")
    UserDetail getUserDetail(int userId);

    @Select("SELECT * FROM user")
    List<User> getAllUserList();

    @Update("UPDATE user SET nickname = #{nickname} WHERE user_id = #{userId}")
    void updateUser(UpdateUser updateUser);

    @Update("UPDATE user SET fan_count = #{fanCount}, follow_count = #{followCount}, collect_count = #{collectCount}, post_count = #{postCount}, like_count = #{likeCount} WHERE user_id = #{userId}")
    void updateUserMetadata(UpdateUserMetadata updateUserMetadata);
}
