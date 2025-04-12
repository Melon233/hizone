package com.example.hizone.table.user;

import com.example.hizone.dto.UpdateUserMetadata;

import lombok.Data;

@Data
public class UserMetadata {

    private Long userId;

    private Long fanCount;

    private Long followCount;

    private Long collectCount;

    private Long postCount;

    private Long likedCount;

    public UserMetadata merge(UpdateUserMetadata userMetadata) {
        this.fanCount += userMetadata.getFanCount();
        this.followCount += userMetadata.getFollowCount();
        this.collectCount += userMetadata.getCollectCount();
        this.postCount += userMetadata.getPostCount();
        this.likedCount += userMetadata.getLikedCount();
        return this;
    }
}
