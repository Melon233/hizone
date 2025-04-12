package com.example.hizone.request.follow;

import lombok.Data;

@Data
public class DeleteFollow {

    private Long followerId;

    private Long followeeId;
}
