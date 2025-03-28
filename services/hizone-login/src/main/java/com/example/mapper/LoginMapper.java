package com.example.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.example.fenta.dao.user.User;

@Mapper
public interface LoginMapper {

    @Insert("INSERT INTO user (nickname, email) VALUES (#{nickname}, #{email})")
    void insertUser(User user);

    @Select("SELECT * FROM user WHERE email = #{email}")
    User selectUserByEmail(String email);
}
