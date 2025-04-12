package com.example.hizone.table.user;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class User {

    private Long userId;

    private String nickname;

    private String email;

    private String registerTime;
}
