package com.example.fenta.dao.user;

import com.example.fenta.inter.UpdateUserMetadata;

import lombok.Data;

@Data
public class UserMetadata {

    private int userId;

    private int fanCount;

    private int followCount;

    private int collectCount;

    private int postCount;

    private int likedCount;

    public UserMetadata merge(UpdateUserMetadata userMetadata) {
        this.fanCount += userMetadata.getFanCount();
        this.followCount += userMetadata.getFollowCount();
        this.collectCount += userMetadata.getCollectCount();
        this.postCount += userMetadata.getPostCount();
        this.likedCount += userMetadata.getLikedCount();
        return this;
    }
}
