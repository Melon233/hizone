package com.example.hizone.request.follow;

import lombok.Data;

@Data
public class AddFollow {

    private Long followerId;

    private Long followeeId;
}
