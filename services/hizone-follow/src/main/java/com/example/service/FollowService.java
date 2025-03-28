package com.example.service;

import java.util.List;

import com.example.fenta.dao.follow.Follow;
import com.example.fenta.front.follow.AddFollow;
import com.example.fenta.front.follow.DeleteFan;
import com.example.fenta.front.follow.DeleteFollow;

public interface FollowService {

    List<Follow> getFanList(int userId);

    List<Follow> getFollowList(int userId);

    void addFollow(AddFollow addFollow);

    void deleteFan(DeleteFan deleteFan);

    void deleteFollow(DeleteFollow deleteFollow);
}
