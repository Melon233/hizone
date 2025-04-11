package com.example.hizone.dao.follow;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Follow {

    private int followerId;

    private int followeeId;
}
