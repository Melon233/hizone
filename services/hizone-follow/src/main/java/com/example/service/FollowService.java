package com.example.service;

import java.util.List;

import com.example.hizone.dao.follow.Follow;
import com.example.hizone.front.follow.AddFollow;
import com.example.hizone.front.follow.DeleteFan;
import com.example.hizone.front.follow.DeleteFollow;

public interface FollowService {

    List<Follow> getFanList(int userId);

    List<Follow> getFollowList(int userId);

    void addFollow(AddFollow addFollow);

    void deleteFan(DeleteFan deleteFan);

    void deleteFollow(DeleteFollow deleteFollow);

    boolean hasFollow(Follow follow);
}
