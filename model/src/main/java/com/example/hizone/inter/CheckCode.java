package com.example.hizone.inter;

import lombok.Data;

@Data
public class CheckCode {

    private String email;

    private int checkCode;

    private long expireTime;
}
