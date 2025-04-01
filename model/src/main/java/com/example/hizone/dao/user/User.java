package com.example.hizone.dao.user;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class User {

    private int userId;

    private String nickname;

    private String email;

    private String registerTime;
}
