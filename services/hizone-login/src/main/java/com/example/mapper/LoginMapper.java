package com.example.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import com.example.hizone.table.user.User;

@Mapper
public interface LoginMapper {

    @Options(useGeneratedKeys = true, keyProperty = "userId")
    @Insert("INSERT INTO user (nickname, email) VALUES (#{nickname}, #{email})")
    void insertUser(User user);

    @Select("SELECT * FROM user WHERE email = #{email}")
    User selectUserByEmail(String email);

    @Insert("INSERT INTO user_metadata (user_id) VALUES (#{userId})")
    void insertNewUserMetadata(Long userId);
}
