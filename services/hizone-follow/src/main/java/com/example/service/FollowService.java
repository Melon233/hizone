package com.example.service;

import java.util.List;

import com.example.hizone.request.follow.AddFollow;
import com.example.hizone.request.follow.DeleteFan;
import com.example.hizone.request.follow.DeleteFollow;
import com.example.hizone.table.follow.Follow;

public interface FollowService {

    List<Follow> getFanList(int userId);

    List<Follow> getFollowList(int userId);

    void addFollow(AddFollow addFollow);

    void deleteFan(DeleteFan deleteFan);

    void deleteFollow(DeleteFollow deleteFollow);

    boolean hasFollow(Follow follow);
}
