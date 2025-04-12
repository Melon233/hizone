package com.example.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.hizone.request.follow.AddFollow;
import com.example.hizone.request.follow.DeleteFan;
import com.example.hizone.request.follow.DeleteFollow;
import com.example.hizone.table.follow.Follow;
import com.example.mapper.FollowMapper;
import com.example.service.FollowService;

@Service
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

    @Override
    public boolean hasFollow(Follow follow) {
        return followMapper.selectFollow(follow) != null;
    }
}
