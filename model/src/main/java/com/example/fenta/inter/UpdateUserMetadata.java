package com.example.fenta.inter;

import lombok.Data;

@Data
public class UpdateUserMetadata {

    private int userId;

    private int fanCount;

    private int followCount;

    private int collectCount;

    private int postCount;

    private int likedCount;
}
