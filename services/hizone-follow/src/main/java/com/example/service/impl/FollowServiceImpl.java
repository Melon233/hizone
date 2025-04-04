package com.example.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.hizone.dao.follow.Follow;
import com.example.hizone.front.follow.AddFollow;
import com.example.hizone.front.follow.DeleteFan;
import com.example.hizone.front.follow.DeleteFollow;
import com.example.mapper.FollowMapper;
import com.example.service.FollowService;

public class FollowServiceImpl implements FollowService {

    @Autowired
    private FollowMapper followMapper;

    @Override
    public List<Follow> getFanList(int userId) {
        return followMapper.selectFanList(userId);
    }

    @Override
    public List<Follow> getFollowList(int userId) {
        return followMapper.selectFollowList(userId);
    }

    @Override
    public void deleteFan(DeleteFan deleteFan) {
        followMapper.deleteFan(deleteFan);
    }

    @Override
    public void deleteFollow(DeleteFollow deleteFollow) {
        followMapper.deleteFollow(deleteFollow);
    }

    @Override
    public void addFollow(AddFollow addFollow) {
        followMapper.insertFollow(addFollow);
    }
}
