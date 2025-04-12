package com.example.hizone.request.follow;

import lombok.Data;

@Data
public class DeleteFan {

    private Long followerId;

    private Long followeeId;
}
